package com.perf.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.perf.backend.entity.Administrator;
import com.perf.backend.mapper.AdministratorMapper;

@Service
public class AdministratorService {
    
    @Autowired
    private AdministratorMapper administratorMapper;
    
    public Administrator findByUsername(String username) {
        LambdaQueryWrapper<Administrator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Administrator::getUsername, username);
        return administratorMapper.selectOne(wrapper);
    }
    
    public boolean save(Administrator administrator) {
        // 设置创建时间和更新时间
        administrator.setCreatedDate(LocalDateTime.now());
        administrator.setUpdateDate(LocalDateTime.now());
        return administratorMapper.insert(administrator) > 0;
    }
    
    public Administrator getById(Integer id) {
        return administratorMapper.selectById(id);
    }
}