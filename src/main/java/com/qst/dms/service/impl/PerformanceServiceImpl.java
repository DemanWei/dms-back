package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.Performance;
import com.qst.dms.mapper.PerformanceMapper;
import com.qst.dms.service.PerformanceService;
import org.springframework.stereotype.Service;

@Service
public class PerformanceServiceImpl extends ServiceImpl<PerformanceMapper, Performance> implements PerformanceService {
}
