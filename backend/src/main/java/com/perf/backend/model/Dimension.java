package com.perf.backend.model;

import java.util.List;

public class Dimension {
  private String dimension;
  private List<Question> questions;

  public Dimension() {}

  public Dimension(String dimension, List<Question> questions) {
    this.dimension = dimension;
    this.questions = questions;
  }

  public String getDimension() {
    return dimension;
  }

  public void setDimension(String dimension) {
    this.dimension = dimension;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }
}