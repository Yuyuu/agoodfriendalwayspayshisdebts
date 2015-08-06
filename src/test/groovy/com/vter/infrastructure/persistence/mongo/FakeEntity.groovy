package com.vter.infrastructure.persistence.mongo

import com.vter.model.Entity

class FakeEntity implements Entity<String> {

  String id;

  @SuppressWarnings("GroovyUnusedDeclaration")
  public FakeEntity() {}

  public FakeEntity(String id) {
    this.id = id
  }

  @Override
  String getId() {
    return id
  }
}
