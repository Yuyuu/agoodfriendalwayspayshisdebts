package com.vter.search;

public enum PaginationTarget {
  NEXT("next"),
  LAST("last"),
  FIRST("first"),
  PREVIOUS("prev");

  private final String relation;

  PaginationTarget(String relation) {
    this.relation = relation;
  }

  @Override
  public String toString() {
    return relation;
  }
}
