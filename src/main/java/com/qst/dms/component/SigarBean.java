package com.qst.dms.component;

import org.hyperic.sigar.Sigar;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SigarBean {
    @Bean
    public Sigar sigar() {
        return new Sigar();
    }
}
