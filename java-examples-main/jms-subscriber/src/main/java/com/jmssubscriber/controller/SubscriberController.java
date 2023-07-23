package com.jmssubscriber.controller;

import com.jmssubscriber.service.JmsSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriberController {
    @Autowired
    JmsSubscriberService jmsSubscriberService;

    @PostMapping("subscribe/{topicName}")
    public String startSubscriber(@PathVariable(value = "topicName", required = true) String topicName){
        return jmsSubscriberService.startSubscriber(topicName);
    }

    @PostMapping("unsubscribe/{topicName}")
    public String shutdownTopicSubscriber(@PathVariable(value = "topicName", required = true) String topicName){
        return jmsSubscriberService.shutdownTopicSubscriber(topicName);
    }

    @PostMapping("unsubscribe/all")
    public String shutdownAllSubscribers(){
        return jmsSubscriberService.shutdownAllSubscribers();
    }
}
