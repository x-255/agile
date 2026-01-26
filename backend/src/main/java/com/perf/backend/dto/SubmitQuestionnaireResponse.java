package com.perf.backend.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuestionnaireResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer reportId;
  private Integer recordId;
  private Integer userId;
  private String userName;
  private List<DimensionScore> dimensionScores;
  private Integer comprehensiveScore;
  private String maturityLevel;
}