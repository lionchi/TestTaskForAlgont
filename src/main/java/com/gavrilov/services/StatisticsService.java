package com.gavrilov.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService extends AbstractService {

    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${max.value.cpu}")
    private String maxValue;

    @Autowired
    public StatisticsService(RabbitTemplate rabbitTemplate, SimpMessagingTemplate messagingTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void run() {
        int processCpuLoad = getProcessCpuLoad();
        if (processCpuLoad != 0) {
            String result;
            if (processCpuLoad < Integer.valueOf(maxValue)) {
                result = processCpuLoad + "%";
                messagingTemplate.convertAndSend("/topic/statistic", result);
            } else {
                result = "CPU loaded";
                rabbitTemplate.setExchange("rabbit-fanout-exchange");
                rabbitTemplate.convertAndSend(result);
            }
        }
    }
}
