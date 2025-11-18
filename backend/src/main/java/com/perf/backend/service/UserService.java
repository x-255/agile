package com.perf.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.perf.backend.entity.User;
import com.perf.backend.mapper.UserMapper;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
    
    public boolean save(User user) {
        // 设置创建时间和更新时间
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        return userMapper.insert(user) > 0;
    }
    
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }
}