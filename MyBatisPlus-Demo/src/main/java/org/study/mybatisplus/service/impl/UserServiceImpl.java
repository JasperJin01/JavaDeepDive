package org.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.study.mybatisplus.dao.UserMapper;
import org.study.mybatisplus.entity.UserEntity;
import org.study.mybatisplus.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}
