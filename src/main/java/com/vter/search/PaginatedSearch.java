package com.vter.search;

public abstract class PaginatedSearch<TResponse> implements Search<TResponse> {

  public void page(int page) {
    this.page = page;
  }

  public int page() {
    return page;
  }

  private int page;
}
