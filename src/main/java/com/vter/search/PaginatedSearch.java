package com.vter.search;

public abstract class PaginatedSearch<TResponse> implements Search<TResponse> {

  public PaginatedSearch<TResponse> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public PaginatedSearch<TResponse> skip(int skip) {
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
