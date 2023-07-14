package com.qst.dms.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("performance")
public class Performance {
    private Long id;
    private Double cpuUsage;
    private Long memoryTotal;
    private Long memoryUsed;
    private String memoryUnit;
    private Long netRx;
    private Long netTx;
    private String netUnit;
    private Long diskTotal;
    private Long diskUsed;
    private String diskUnit;
    private Long heapUsed;
    private Long heapMax;
    private String heapUnit;
    private Long nonHeapUsed;
    private Long nonHeapMax;
    private String nonHeapUnit;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
