package com.jmspublisher.model;

import com.solacesystems.jms.message.SolTextMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
@Scope("prototype")
@Getter
@Setter
public class MyMessage {
    private String topicName;
    private String data;
}
