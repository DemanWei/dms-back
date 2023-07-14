package com.qst.dms.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qst.dms.domain.OperLog;
import com.qst.dms.service.OperLogService;
import com.qst.dms.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operLog")
@Api(tags = "操作日志管理接口")
public class OperLogController {
    @Autowired
    private OperLogService operLogService;

    @GetMapping("/byUser/")
    @ApiOperation("根据userId分页查询操作日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", example = "?userId=1"),
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1", required = false, paramType = "query", example = "&current=1"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "20", required = false, paramType = "query", example = "&size=20")
    })
    public R<IPage<OperLog>> listByUser(Long userId, Long current, Long size) {
        if (current == null || current <= 0) {
            current = 1L;
        }
        if (size == null || size <= 0) {
            size = 20L;
        }
        IPage<OperLog> iPage = new Page<>(current, size);
        Wrapper<OperLog> wrapper = new LambdaQueryWrapper<OperLog>()
                .eq(userId != null, OperLog::getUserId, userId)
                .orderByDesc(OperLog::getCreateTime);
        operLogService.page(iPage, wrapper);
        return R.success(iPage);
    }
}
