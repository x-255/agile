package com.perf.backend.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImplementationAnalysisData implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private List<RoadmapPhase> roadmap;
  private Suggest suggest;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoadmapPhase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String timeSlot;
    private String description;
    private List<String> actions;
  }
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Suggest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private String promote;
    private String keysToSuccess;
    private String resourceInvestment;
  }
}