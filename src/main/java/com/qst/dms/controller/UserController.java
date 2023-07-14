package com.qst.dms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qst.dms.domain.User;
import com.qst.dms.service.UserService;
import com.qst.dms.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public R<List<User>> list() {
        List<User> userList = userService.list();
        return R.success(userList);
    }

    @GetMapping("/get/{userId}")
    @ApiOperation("根据id获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "path", example = "/1")
    })
    public R<User> list(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return R.success(user);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户名+密码", required = true, paramType = "body",
                    example = "{\"username\":\"90217\", \"password\": \"123abc\"}")
    })
    public R<User> login(@RequestBody User user) {
        if (user == null || user.getUsername() == null || StrUtil.isEmpty(user.getPassword())) {
            return R.error("请求字段缺失");
        }

//        String passwordMd5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
//                .eq(User::getUsername, user.getUsername())
//                .eq(User::getPassword, passwordMd5);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword());
        User targetUser = userService.getOne(queryWrapper);
        if (targetUser == null) {
            return R.error("账号或密码错误");
        }
        return R.success(targetUser);
    }
}
