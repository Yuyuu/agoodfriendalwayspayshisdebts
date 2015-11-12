package com.vter.search;

public abstract class PaginatedSearch<TResponse> implements Search<TResponse> {

  public PaginatedSearch<TResponse> page(int page) {
    this.page = page;
    return this;
  }

  public int page() {
    return page;
  }

  private int page;
}
