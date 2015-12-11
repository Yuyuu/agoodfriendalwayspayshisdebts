package com.vter.infrastructure.persistence.mongo

import com.vter.model.Aggregate

class FakeAggregate implements Aggregate<String> {

  @SuppressWarnings("GroovyUnusedDeclaration")
  public FakeAggregate() {}

  public FakeAggregate(String id) {
    this.id = id
  }

  @Override
  String getId() {
    return id
  }

  String id
}
