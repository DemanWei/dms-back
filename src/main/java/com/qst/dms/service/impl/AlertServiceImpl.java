package com.qst.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.dms.domain.Alert;
import com.qst.dms.mapper.AlertMapper;
import com.qst.dms.service.AlertService;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl extends ServiceImpl<AlertMapper, Alert> implements AlertService {
}
