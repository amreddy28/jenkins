package com.jmssubscriber.listener;

import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jms.message.SolTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MessageSubscriberListener implements MessageListener {
    Logger logger = LoggerFactory.getLogger(MessageSubscriberListener.class);
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            logger.info(String.format("TextMessage received: '%s'%n", ((TextMessage) message).getText()));
        }else if (message instanceof SolTextMessage) {
            try {
                logger.info(String.format("TextMessage received: '%s'%n", ((SolTextMessage) message).getText()));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.info(String.format("Message received."));
        }
        //logger.info(String.format("Message Content:%n%s%n", SolJmsUtility.dumpMessage(message)));
    }
}
