package com.gavrilov.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.*;
import java.lang.management.ManagementFactory;

@Service
public class StatisticsService implements Runnable {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public StatisticsService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run() {
        double processCpuLoad = getProcessCpuLoad();
        String result;
        if (processCpuLoad < 1.0) {
            result = "The CPU load is " + processCpuLoad + "%";
        } else {
            result = "CPU loaded more than 3%";
        }
        rabbitTemplate.setExchange("rabbit-fanout-exchange");
        rabbitTemplate.convertAndSend(result);
    }

    private double getProcessCpuLoad() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            // Получаем имя объекта MBean
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            // Получаем список значений для MBean type=OperatingSystem
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
            if (list.isEmpty()) {
                return Double.NaN;
            }
            Attribute attribute = (Attribute) list.get(0);
            Double value = (Double) attribute.getValue();
            if (value == -1.0) {
                return Double.NaN;
            }
            // возвращает процентное значение с точностью до 1 десятичной
            return ((int) (value * 1000) / 10.0);
        } catch (MalformedObjectNameException | InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
