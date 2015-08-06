package com.vter.infrastructure.persistence.mongo;

import com.google.common.reflect.TypeToken;
import com.vter.model.Entity;
import com.vter.model.Repository;
import org.mongolink.MongoSession;

public abstract class MongoLinkRepository<TId, TEntity extends Entity<TId>> implements Repository<TId, TEntity> {

  private final MongoSession session;
  private final TypeToken<TEntity> typeToken = new TypeToken<TEntity>(getClass()) {
  };

  protected MongoLinkRepository(MongoSession session) {
    this.session = session;
  }

  @Override
  public TEntity get(TId id) {
    return session.get(id, entityType());
  }

  @Override
  public void save(TEntity entity) {
    session.save(entity);
  }

  @Override
  public void delete(TEntity entity) {
    session.delete(entity);
  }

  protected Class<TEntity> entityType() {
    return (Class<TEntity>) typeToken.getRawType();
  }
}
