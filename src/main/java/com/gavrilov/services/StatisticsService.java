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

    // Второй способ получения
   /* private double getProcessCpuLoad() {
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
    }*/
}
