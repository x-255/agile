package com.perf.backend.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuestionnaireRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String phone;
  private String email;
  private String teamSize;
  private String companySize;
  private String identityType;
  private String industry;
  private List<String> improvementGoal;
  private Long startTime;
  private Long endTime;
  private List<QuestionnaireItem> questionnaireJson;
}