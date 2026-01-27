package com.perf.backend.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveAnalysisData implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String overallAssessment;
  private String keyStrengths;
  private String keyWeaknesses;
  private String industrialPosition;
  private String strategicPositioning;
  private String leverageAnalysis;
  private String roiFocus;
  private ComprehensiveScoreInfo comprehensiveScoreInfo;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ComprehensiveScoreInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer score;
    private Integer industryAverage;
    private Integer industryBenchmark;
  }
}