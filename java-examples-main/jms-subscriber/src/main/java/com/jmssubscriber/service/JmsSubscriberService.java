package com.jmssubscriber.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JmsSubscriberService {
    public String startSubscriber(String topicName);
    public String shutdownTopicSubscriber(String topicName);
    public String shutdownAllSubscribers();
}
