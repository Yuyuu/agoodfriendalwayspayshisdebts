package com.vter.search;

import com.vter.infrastructure.bus.Message;

public abstract class Search<TResponse> implements Message<TResponse> {

  public Search<TResponse> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public Search<TResponse> skip(int skip) {
    this.skip = skip;
    return this;
  }

  private int limit;
  private int skip;
}
