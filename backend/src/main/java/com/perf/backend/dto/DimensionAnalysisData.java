package com.perf.backend.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionAnalysisData implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String dimension;
  private String detailedAnalysis;
  private List<String> strengths;
  private List<String> weaknesses;
}