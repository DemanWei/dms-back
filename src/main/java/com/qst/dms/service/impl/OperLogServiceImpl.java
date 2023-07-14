package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.OperLog;
import com.qst.dms.mapper.OperLogMapper;
import com.qst.dms.service.OperLogService;
import org.springframework.stereotype.Service;

@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
}
