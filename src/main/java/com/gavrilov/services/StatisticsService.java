package com.gavrilov.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Service
public class StatisticsService implements Runnable {

    private final RabbitTemplate rabbitTemplate;

    @Value("${max.value.cpu}")
    private String maxValue;

    @Autowired
    public StatisticsService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run() {
        int processCpuLoad = getProcessCpuLoad();
        String result;
        if (processCpuLoad < Integer.valueOf(maxValue)) {
            result = "The CPU load is " + processCpuLoad + "%";
        } else {
            result = "CPU loaded";
        }
        rabbitTemplate.setExchange("rabbit-fanout-exchange");
        rabbitTemplate.convertAndSend(result);
    }

    private int getProcessCpuLoad() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double value = 0.0;
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals("getSystemCpuLoad") && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (double) method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return value < 0 ? 0 : ((int) (value * 100));
    }
}
