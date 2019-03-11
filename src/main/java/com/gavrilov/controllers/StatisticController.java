package com.gavrilov.controllers;

import com.gavrilov.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatisticController {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statisticsCpu")
    public ResponseEntity<String> getStatistics() throws Exception {
        double processCpuLoad = statisticsService.getProcessCpuLoad();
        String result = String.format("Загрузка CPU составляет %s", processCpuLoad);
        return ResponseEntity.ok(result);
    }
}
