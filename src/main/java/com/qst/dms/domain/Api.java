package com.qst.dms.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api")
public class Api {
    private Long id;
    private String path;
    private String method;
    private String paramType;
    private String paramExample;
    private String responseBody;
    private String describe;
    private Long visit;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
