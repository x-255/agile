package com.perf.backend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireItem {
  private String dimension;
  private List<Question> questions;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Question {
    private String content;
    private List<String> options;
  }
}