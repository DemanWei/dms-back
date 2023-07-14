package com.qst.dms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qst.dms.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
