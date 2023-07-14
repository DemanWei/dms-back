package com.qst.dms.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** 元数据对象处理器,MP提供的公共字段自动填充的类 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        /** 插入时自动填充 */
        metaObject.setValue("createTime", LocalDateTime.now());
        if(metaObject.hasGetter("updateTime") && metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        /** 更新时自动填充 */
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
