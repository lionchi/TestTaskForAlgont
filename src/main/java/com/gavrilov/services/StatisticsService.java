package com.gavrilov.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@Service
public class StatisticsService {
    @Async
    public double getProcessCpuLoad() throws Exception {

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
        // возвращает процентное значение с точностью до 1 десятичной точки
        return ((int) (value * 1000) / 10.0);
    }
}
