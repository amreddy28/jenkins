package com.jmspublisher.controller;

import com.jmspublisher.model.MyMessage;
import com.jmspublisher.service.JmsPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PublisherController {
    @Autowired
    JmsPublisherService jmsPublisherService;

    @PostMapping("publisher/start")
    public ResponseEntity<String> startPublisher(){
        String ackMsg = jmsPublisherService.startPublisherService();
        return ResponseEntity.ok(ackMsg);
    }

    @PostMapping("publisher/stop")
    public ResponseEntity<String> stopPublisher(){
        String ackMsg = jmsPublisherService.stopPublisherService();
        return ResponseEntity.ok(ackMsg);
    }

    @PostMapping("publisher/push")
    public ResponseEntity<MyMessage> publish(@RequestBody MyMessage message){
        if(message == null){
            return ResponseEntity.badRequest().build();
        }
        MyMessage cratedMsg = jmsPublisherService.publishMessage(message);
        return ResponseEntity.ok(cratedMsg);
    }
}
