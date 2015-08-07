package com.vter.infrastructure.persistence.memory;

import com.google.common.collect.Sets;
import com.vter.model.Entity;
import com.vter.model.Repository;

import java.util.Set;

public class MemoryRepository<TId, TEntity extends Entity<TId>> implements Repository<TId, TEntity> {

  @Override
  public TEntity get(TId tId) {
    return entities.stream().filter(entity -> entity.getId().equals(tId)).findFirst().get();
  }

  @Override
  public void save(TEntity entity) {
    entities.add(entity);
  }

  @Override
  public void delete(TEntity entity) {
    entities.remove(entity);
  }

  protected final Set<TEntity> entities = Sets.newHashSet();
}
