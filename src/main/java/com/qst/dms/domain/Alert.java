package com.qst.dms.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("alert")
public class Alert {
    private Long id;
    private String metricName;
    private Long metricValueReal;
    private Long metricValueRule;
    private String metricUnit;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
