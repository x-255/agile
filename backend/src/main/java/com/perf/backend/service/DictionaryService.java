package com.perf.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.perf.backend.entity.Dictionary;
import com.perf.backend.mapper.DictionaryMapper;

@Service
public class DictionaryService {
    
    @Autowired
    private DictionaryMapper dictionaryMapper;
    
    public List<Dictionary> getByCategory(String category) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dictionary::getCategory, category);
        wrapper.orderByAsc(Dictionary::getOrder);
        return dictionaryMapper.selectList(wrapper);
    }
    
    public List<Dictionary> getAll() {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dictionary::getCategory);
        wrapper.orderByAsc(Dictionary::getOrder);
        return dictionaryMapper.selectList(wrapper);
    }
}