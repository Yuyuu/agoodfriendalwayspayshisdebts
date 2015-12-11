package com.vter.model;

public abstract class BaseAggregate<TId> extends BaseEntity<TId> implements Aggregate<TId> {

  protected BaseAggregate() {
  }

  protected BaseAggregate(TId id) {
    super(id);
  }
}
