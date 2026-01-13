package com.perf.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.perf.backend.dto.FrontDictionaryVO;
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

    public List<FrontDictionaryVO> getFrontByCategory(String category) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dictionary::getCategory, category);
        wrapper.orderByAsc(Dictionary::getOrder);
        List<Dictionary> dictionaries = dictionaryMapper.selectList(wrapper);
        return dictionaries.stream().map(this::convertToFrontVO).collect(Collectors.toList());
    }

    public List<FrontDictionaryVO> getAllFront() {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dictionary::getCategory);
        wrapper.orderByAsc(Dictionary::getOrder);
        List<Dictionary> dictionaries = dictionaryMapper.selectList(wrapper);
        return dictionaries.stream().map(this::convertToFrontVO).collect(Collectors.toList());
    }

    private FrontDictionaryVO convertToFrontVO(Dictionary dictionary) {
        FrontDictionaryVO vo = new FrontDictionaryVO();
        vo.setCode(dictionary.getCode());
        vo.setName(dictionary.getName());
        return vo;
    }
}