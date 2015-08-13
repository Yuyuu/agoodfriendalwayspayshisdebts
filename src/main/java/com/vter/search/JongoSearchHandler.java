package com.vter.search;

import org.jongo.Jongo;

import javax.inject.Inject;

public abstract class JongoSearchHandler<TSearch extends Search<TResponse>, TResponse> implements SearchHandler<TSearch, TResponse> {

  @Override
  public TResponse execute(TSearch search) {
    return execute(search, jongo);
  }

  protected abstract TResponse execute(TSearch search, Jongo jongo);

  @Inject
  public void setJongo(Jongo jongo) {
    this.jongo = jongo;
  }

  private Jongo jongo;
}
