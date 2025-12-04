package com.perf.backend.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireItem implements Serializable {
  private static final long serialVersionUID = 1L;
  private String dimension;
  private List<Question> questions;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private List<String> options;
  }
}