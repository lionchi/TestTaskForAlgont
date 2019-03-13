package com.gavrilov.controllers;

import com.gavrilov.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class StatisticController {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @MessageMapping("/cpu")
    @SendTo("/topic/statistic")
    public String getStatisticsCpu(@Payload String message) throws Exception {
        double processCpuLoad = statisticsService.getProcessCpuLoad();
        return "The CPU load is " + processCpuLoad + "%";
    }
}
