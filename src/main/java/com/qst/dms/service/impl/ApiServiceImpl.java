package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.Api;
import com.qst.dms.mapper.ApiMapper;
import com.qst.dms.service.ApiService;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl extends ServiceImpl<ApiMapper, Api> implements ApiService {
}
