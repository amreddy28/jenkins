package com.jmssubscriber.service;

import com.jmssubscriber.listener.MessageSubscriberListener;
import com.solacesystems.jcsmp.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.*;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SolaceSubscriberServiceImpl implements JmsSubscriberService{
    Logger logger = LoggerFactory.getLogger(SolaceSubscriberServiceImpl.class);
    ConcurrentHashMap<String, MessageConsumer> consumersMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Topic> consumersTopicMap = new ConcurrentHashMap<>();

    @Value("${solace.host}")
    private String host;
    @Value("${solace.vpnName}")
    private String vpnName;
    @Value("${solace.userName}")
    private String userName;
    @Value("${solace.pwd}")
    private String pwd;
    @Value("${solace.topic:solveit/inventory}")
    private String defaultTopicName;
    private Connection connection = null;
    private Session session = null;

    @Override
    public String startSubscriber(String topicName) {
        try {
            String returnMsg =String.format("Subscriber connected to events on topic %s. Awaiting message... %n", topicName);
            if(consumersMap.containsKey(topicName)){
                return returnMsg;
            }

            SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
            connectionFactory.setHost(host);
            connectionFactory.setVPN(vpnName);
            connectionFactory.setUsername(userName);
            connectionFactory.setPassword(pwd);

            // Create connection to the Solace router
            connection = connectionFactory.createConnection();

            if(session == null) {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            }
            logger.info(String.format("Connected to Solace Message VPN '%s' with client username '%s'.%n", vpnName,
                    userName));

            Topic topic;
            if(consumersTopicMap.containsKey(topicName)){
                topic = consumersTopicMap.get(topicName);
            }else{
                topic = session.createTopic(topicName);
                consumersTopicMap.put(topicName, topic);
            }

            MessageConsumer messageConsumer = session.createConsumer(topic);
            messageConsumer.setMessageListener(new MessageSubscriberListener());

            // Start receiving messages
            connection.start();
            logger.info(returnMsg);
            consumersMap.put(topicName, messageConsumer);
            return returnMsg;
        } catch (InvalidPropertiesException e) {
            throw new RuntimeException(e);
        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String shutdownTopicSubscriber(String topicName) {
        String returnMsg =String.format("Subscriber to event on topic '%s' is now stopped.%n", topicName);
        if(consumersMap.containsKey(topicName) && session!= null){
            MessageConsumer consumer = consumersMap.get(topicName);
            try {
                consumer.close();
                consumersMap.remove(topicName);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info(returnMsg);
        return returnMsg;
    }

    @Override
    public String shutdownAllSubscribers() {
        try {
            if (consumersMap != null) {
                consumersMap.keySet().stream().forEach(k -> {
                    try {
                        consumersMap.get(k).close();
                        consumersMap.remove(k);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            // Close everything in the order reversed from the opening order
            // NOTE: as the interfaces below extend AutoCloseable,
            // with them it's possible to use the "try-with-resources" Java statement
            // see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html

            session.close();
            connection.close();
            logger.info("All Subscribers are now stopped.%n");
            return "All Subscribers are now stopped.";
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
