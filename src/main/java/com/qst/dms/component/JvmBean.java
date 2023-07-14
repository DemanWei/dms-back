package com.qst.dms.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

@Component
public class JvmBean {
    @Bean
    public MemoryUsage heapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    }

    @Bean
    public MemoryUsage nonHeapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
    }
}
