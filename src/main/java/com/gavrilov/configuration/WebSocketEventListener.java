package com.gavrilov.configuration;

import com.gavrilov.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Component
public class WebSocketEventListener {
    private final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private Set<String> connection = new HashSet<>();
    private Set<ScheduledFuture<?>> schedules = new HashSet<>();

    private final ThreadPoolTaskScheduler taskScheduler;
    private final CronTrigger cronTrigger;
    private final StatisticsService statisticsService;

    @Autowired
    public WebSocketEventListener(ThreadPoolTaskScheduler taskScheduler, CronTrigger cronTrigger, StatisticsService statisticsService) {
        this.taskScheduler = taskScheduler;
        this.cronTrigger = cronTrigger;
        this.statisticsService = statisticsService;
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (connection.size() == 0) {
            ScheduledFuture<?> schedule = taskScheduler.schedule(statisticsService, cronTrigger);
            schedules.add(schedule);
        }
        connection.add((String) headerAccessor.getMessageHeaders().get("simpSessionId"));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        connection.remove((String) headerAccessor.getMessageHeaders().get("simpSessionId"));
        if (connection.size() == 0) {
            schedules.forEach(scheduledFuture -> {
                if (!scheduledFuture.isCancelled()) {
                    scheduledFuture.cancel(true);
                }
            });
            schedules.clear();
        }
        logger.info("Received a web socket disconnected");
    }
}
