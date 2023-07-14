package com.qst.dms.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qst.dms.domain.Performance;
import com.qst.dms.service.PerformanceService;
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
@RequestMapping("/performance")
@Api(tags = "监控信息管理接口")
public class PerformanceController {
    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/recent")
    @ApiOperation("监控记录分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1", required = false, paramType = "query",
                    example = "?current=1"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "20", required = false, paramType = "query",
                    example = "&size=20")
    })
    public R<IPage<Performance>> recent(Long current, Long size) {
        if (current == null || current <= 0) {
            current = 1L;
        }
        if (size == null || size <= 0) {
            size = 20L;
        }
        IPage<Performance> iPage = new Page<>(current, size);
        Wrapper<Performance> wrapper = new LambdaQueryWrapper<Performance>()
                .orderByDesc(Performance::getCreateTime);
        performanceService.page(iPage, wrapper);
        return R.success(iPage);
    }
}
