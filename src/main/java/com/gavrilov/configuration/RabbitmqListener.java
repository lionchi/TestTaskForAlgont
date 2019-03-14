package com.gavrilov.configuration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqListener {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RabbitmqListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "rabbit-queue")
    public void worker(String message) {
        messagingTemplate.convertAndSend("/topic/statistic", message);
    }

}
