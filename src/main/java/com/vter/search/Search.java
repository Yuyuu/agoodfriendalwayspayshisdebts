package com.vter.search;

import com.vter.infrastructure.bus.Message;

public abstract class Search<TResponse> implements Message<TResponse> {

  public Search<TResponse> perPage(int perPage) {
    this.perPage = perPage;
    return this;
  }

  public Search<TResponse> page(int page) {
    this.page = page;
    return this;
  }

  public int skip() {
    return (page - 1) * perPage;
  }

  public int page() {
    return page;
  }

  public int perPage() {
    return perPage;
  }

  private int perPage;
  private int page;
}
