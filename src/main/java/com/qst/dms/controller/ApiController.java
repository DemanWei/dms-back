package com.qst.dms.controller;

import cn.hutool.json.JSONUtil;
import com.qst.dms.domain.Api;
import com.qst.dms.service.ApiService;
import com.qst.dms.utils.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@io.swagger.annotations.Api(tags = "Api管理接口")
public class ApiController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @GetMapping("/list")
    @ApiOperation("获取Api列表")
    public R<List<Api>> list() {
        List<Api> apiList = apiService.list();
        return R.success(apiList);
    }

    @GetMapping("/check")
    @ApiOperation("接口连通性检测")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiId", value = "api的id", required = true, paramType = "query",
                    example = "?apiId=2")
    })
    public R<Integer> check(Long apiId) {
        if (apiId == null) {
            return R.error("请求字段缺失");
        }
        Api api = apiService.getById(apiId);
        if (api == null) {
            return R.error("id有误,未找到该Api");
        }
        // 获取path
        String path = api.getPath();
        // 构建绝对路径,RestTemplate要求是绝对路径
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .toUriString();
        // 访问一次
        String result = restTemplateBuilder.build().getForObject(url, String.class);
        Map<String, Object> response = JSONUtil.toBean(result, Map.class);
        if ((int) response.get("code") == 1) {
            // 处理结果
            return R.success(1);
        }
        return R.error((String) response.get("msg"));
    }

    @PostMapping("/insert")
    @ApiOperation("新增接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "api", value = "接口实体", required = true, paramType = "body",
                    example = "{\"path\": \"/user/logout\", \"method\": \"POST\", \"paramType\": \"body\", " +
                            "\"describe\": \"用户退出\", \"visit\": 0}")
    })
    public R<Object> insert(@RequestBody Api api) {
        apiService.save(api);
        return R.success(null);
    }

    @DeleteMapping("/delete/{apiId}")
    @ApiOperation("根据id删除接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiId", value = "api的id", required = true, paramType = "path",
                    example = "/87")
    })
    public R<Object> delete(@PathVariable Long apiId) {
        apiService.removeById(apiId);
        return R.success(null);
    }

    @PostMapping("/update")
    @ApiOperation("修改接口信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "api", value = "要修改的api实体,必须包含id字段", required = true, paramType = "body",
                    example = "{\"id\": 66, \"method\": \"POST\"}")
    })
    public R<Object> update(@RequestBody Api api) {
        apiService.updateById(api);
        return R.success(null);
    }
}
