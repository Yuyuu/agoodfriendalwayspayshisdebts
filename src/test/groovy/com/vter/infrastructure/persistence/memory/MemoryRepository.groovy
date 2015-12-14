package com.vter.infrastructure.persistence.memory

import com.vter.model.Aggregate
import com.vter.model.Repository

class MemoryRepository<TId, TAggregate extends Aggregate<TId>> implements Repository<TId, TAggregate> {

  @Override
  TAggregate get(TId id) {
    return entities.find { it.id == id }
  }

  @Override
  void add(TAggregate entity) {
    entities << entity
  }

  @Override
  void delete(TAggregate entity) {
    entities.remove(entity)
  }

  public Set<TAggregate> getAll() {
    return entities;
  }

  final Set<TAggregate> entities = []
}
