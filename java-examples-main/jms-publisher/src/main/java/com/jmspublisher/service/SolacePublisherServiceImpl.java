package com.jmspublisher.service;

import com.jmspublisher.model.MyMessage;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SolacePublisherServiceImpl implements JmsPublisherService{
    Logger logger = LoggerFactory.getLogger(SolacePublisherServiceImpl.class);
    private Connection connection = null;
    private Session session = null;

    ConcurrentHashMap<String, Topic> topicMap = new ConcurrentHashMap<>();

    @Value("${solace.host}")
    private String host;
    @Value("${solace.vpnName}")
    private String vpnName;
    @Value("${solace.userName}")
    private String userName;
    @Value("${solace.pwd}")
    private String pwd;
    @Value("${solace.topic:solveit-inventory}")
    private String defaultTopicName;

    @Override
    public String startPublisherService() {
        try {
            SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
            connectionFactory.setHost(host);
            connectionFactory.setVPN(vpnName);
            connectionFactory.setUsername(userName);
            connectionFactory.setPassword(pwd);

            // Create connection to the Solace router
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Started publisher service";
    }

    @Override
    public String stopPublisherService() {
        logger.info(" *********************Shutting down publisher service *********************");
        try {
           if(session!= null){
               session.close();
               logger.info("closing Session for publisher service");
           }
           if(connection!= null){
               connection.close();
               logger.info("closing Connection to Solace from publisher service");
           }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info(" *********************Publisher service is now shutdown*********************");
        return "Stopped publisher service";
    }

    @Override
    public MyMessage publishMessage(MyMessage msg) {
        try {
            if(session == null){
                startPublisherService();
            }
            Topic topic = null;
            if(msg.getTopicName() == null || msg.getTopicName().length() <= 0){
                logger.info("Using default topic name "+ defaultTopicName);
                msg.setTopicName(defaultTopicName);
            }
            if(topicMap.containsKey(msg.getTopicName())){
                topic = topicMap.get(msg.getTopicName());
            }else {
                topic = session.createTopic(msg.getTopicName());
                topicMap.put(msg.getTopicName(), topic);
            }
            MessageProducer messageProducer = session.createProducer(topic);

            // Create the message
            TextMessage message = session.createTextMessage(msg.getData());

            logger.info(String.format("Sending message '%s' to topic '%s'...%n", message.getText(), topic.toString()));

            // Send the message
            // NOTE: JMS Message Priority is not supported by the Solace Message Bus
            messageProducer.send(topic, message, DeliveryMode.NON_PERSISTENT,
                    Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
            System.out.println("Sent successfully. Exiting...");

            // Close everything in the order reversed from the opening order
            // NOTE: as the interfaces below extend AutoCloseable,
            // with them it's possible to use the "try-with-resources" Java statement
            // see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
            messageProducer.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        return msg;
    }
}
