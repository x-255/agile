package com.perf.backend.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionScore implements Serializable {
  private static final long serialVersionUID = 1L;
  private String dimension;
  private Integer score;
}