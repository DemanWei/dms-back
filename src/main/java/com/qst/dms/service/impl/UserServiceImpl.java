package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.User;
import com.qst.dms.mapper.UserMapper;
import com.qst.dms.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
