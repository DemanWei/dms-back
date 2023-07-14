package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.Rule;
import com.qst.dms.mapper.RuleMapper;
import com.qst.dms.service.RuleService;
import org.springframework.stereotype.Service;

@Service
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements RuleService {
}
