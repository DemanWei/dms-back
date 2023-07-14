package com.qst.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // 开启定时任务
@EnableAspectJAutoProxy // 开启AOP自动代理
public class DmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DmsApplication.class, args);
    }
}
