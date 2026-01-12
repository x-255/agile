package com.perf.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.Result;
import com.perf.backend.service.DictionaryService;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/list")
    public Result getDictionaryList(@RequestParam(required = false) String category) {
        try {
            if (category != null && !category.isEmpty()) {
                var result = dictionaryService.getByCategory(category);
                return Result.success(result);
            } else {
                var result = dictionaryService.getAll();
                return Result.success(result);
            }
        } catch (Exception e) {
            return Result.fail(500, "获取字典数据失败: " + e.getMessage());
        }
    }
}