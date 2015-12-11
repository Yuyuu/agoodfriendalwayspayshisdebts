package com.vter.model;

public interface Repository<TId, TAggregate extends Aggregate<TId>> {

  TAggregate get(TId id);

  void add(TAggregate entity);

  void delete(TAggregate entity);
}
