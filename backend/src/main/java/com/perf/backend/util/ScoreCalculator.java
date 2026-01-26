package com.perf.backend.util;

import java.util.List;
import com.perf.backend.dto.DimensionScore;

public class ScoreCalculator {

  private ScoreCalculator() {
  }

  public static Integer calculateDimensionScore(List<Integer> answers) {
    if (answers == null || answers.isEmpty()) {
      return 0;
    }

    double sum = 0;
    for (Integer answer : answers) {
      sum += answer;
    }

    double average = sum / answers.size();
    double percentageScore = average * 20;
    return (int) Math.round(percentageScore);
  }

  public static Integer calculateComprehensiveScore(List<DimensionScore> dimensionScores) {
    if (dimensionScores == null || dimensionScores.isEmpty()) {
      return 0;
    }

    double sum = 0;
    for (DimensionScore dimensionScore : dimensionScores) {
      sum += dimensionScore.getScore();
    }

    double average = sum / dimensionScores.size();
    return (int) Math.round(average);
  }

  public static String determineMaturityLevel(int comprehensiveScore) {
    if (comprehensiveScore >= 0 && comprehensiveScore <= 20) {
      return "L1（初始级）";
    } else if (comprehensiveScore > 20 && comprehensiveScore <= 40) {
      return "L2（基础级）";
    } else if (comprehensiveScore > 40 && comprehensiveScore <= 60) {
      return "L3（规范级）";
    } else if (comprehensiveScore > 60 && comprehensiveScore <= 80) {
      return "L4（优化级）";
    } else if (comprehensiveScore > 80 && comprehensiveScore <= 100) {
      return "L5（卓越级）";
    } else {
      return "未知等级";
    }
  }
}