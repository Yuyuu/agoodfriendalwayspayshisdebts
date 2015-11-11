package com.vter.search;

public abstract class RangedSearch<TResponse> implements Search<TResponse> {

  public RangedSearch<TResponse> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public RangedSearch<TResponse> skip(int skip) {
    this.skip = skip;
    return this;
  }

  public int limit() {
    return limit;
  }

  public int skip() {
    return skip;
  }

  private int limit;
  private int skip;
}
