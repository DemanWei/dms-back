package com.qst.dms.schedule;

import com.qst.dms.domain.Alert;
import com.qst.dms.service.AlertService;
import com.qst.dms.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DbSchedule {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AlertService alertService;

    // 每分钟执行一次
    @Scheduled(cron = "*/20 * * * * ?")
    public void pingPong() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        boolean pong = result != null && result == 1;
        if (!pong) {
            log.error("[{}] MySQL is not responding..", DateTimeUtils.now());
            // 生成预警
            Alert alert = new Alert();
            alert.setMetricName("db_connect");
            alert.setMetricValueReal(0L);
            alert.setMetricValueReal(1L);
            alert.setMetricUnit("boolean");
            alertService.save(alert);
            // 发送邮件
        }
    }
}
