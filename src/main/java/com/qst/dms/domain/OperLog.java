package com.qst.dms.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operlog")
public class OperLog {
    private Long id;
    private Long userId;
    private String path;
    private String param;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
