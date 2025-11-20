package com.perf.backend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireData {
  private String dimension;
  private List<QuestionData> questions;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QuestionData {
    private String content;
    private List<String> answers;
  }
}