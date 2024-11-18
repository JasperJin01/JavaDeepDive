package org.study.mybatisplus.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.study.mybatisplus.entity.UserEntity;

// MybatisPlus可以不写xml注释
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
