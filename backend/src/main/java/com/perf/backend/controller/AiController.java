package com.perf.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.Result;
import com.perf.backend.service.AiService;

@RestController
@RequestMapping("/ai")
public class AiController {

  private final AiService aiService;

  public AiController(AiService aiService) {
    this.aiService = aiService;
  }

  @GetMapping("/chat")
  public Result chat() {
    try {
      String response = aiService.generateQuestions(6);
      return Result.success(response);
    } catch (Exception e) {
      return Result.fail(500, "AI服务异常: " + e.getMessage());
    }
  }
}