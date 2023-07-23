package com.jmspublisher.service;

import com.jmspublisher.model.MyMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JmsPublisherService {
    public String startPublisherService();
    public String stopPublisherService();
    public MyMessage publishMessage(MyMessage message);
}
