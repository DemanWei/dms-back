package com.qst.dms.controller;

import com.qst.dms.domain.Rule;
import com.qst.dms.service.RuleService;
import com.qst.dms.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rule")
@Api(tags = "预警规则接口")
public class RuleController {
    @Autowired
    private RuleService ruleService;

    @GetMapping("/list")
    @ApiOperation("获取预警规则列表")
    public R<List<Rule>> list() {
        List<Rule> ruleList = ruleService.list();
        return R.success(ruleList);
    }

    @PostMapping("/insert")
    @ApiOperation("新增预警规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "预警规则实体", required = true, dataType = "body",
                    example = "{\"metricName\": \"cpuUsage\", \"metricValue\": 10, \"metricUnit\": \"percent\"}")
    })
    public R<Object> insert(@RequestBody Rule rule) {
        ruleService.save(rule);
        return R.success(null);
    }

    @PostMapping("/update")
    @ApiOperation("修改预警规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "预警规则实体", required = true, dataType = "body",
                    example = "{\"id\": 1, \"metricName\": \"cpuUsage\", \"metricValue\": 20, \"metricUnit\": \"percent\"}")
    })
    public R<Object> update(@RequestBody Rule rule) {
        ruleService.updateById(rule);
        return R.success(null);
    }

    @DeleteMapping("/delete/{ruleId}")
    @ApiOperation("根据id删除预警规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "规则id", required = true, paramType = "path",
                    example = "/2")
    })
    public R<Object> delete(@PathVariable Long ruleId) {
        ruleService.removeById(ruleId);
        return R.success(null);
    }
}
