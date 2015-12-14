package com.vter.search;

import com.vter.infrastructure.persistence.mongo.MongoSessionProvider;
import org.mongolink.MongoSession;

import javax.inject.Inject;

public abstract class MongoSearchHandler<TSearch extends Search<TResponse>, TResponse> implements SearchHandler<TSearch, TResponse> {

  @Inject
  public void initialize(MongoSessionProvider mongoSessionProvider) {
    this.mongoSessionProvider = mongoSessionProvider;
  }

  public MongoSession session() {
    return mongoSessionProvider.currentSession();
  }

  private MongoSessionProvider mongoSessionProvider;
}
