package com.vter.model;

public interface Repository<TId, TEntity extends Entity<TId>> {

  TEntity get(TId id);

  void save(TEntity entity);

  void delete(TEntity entity);
}
