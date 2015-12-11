package com.vter.infrastructure.persistence.mongo;

import com.google.common.reflect.TypeToken;
import com.vter.model.Aggregate;
import com.vter.model.Repository;
import org.mongolink.MongoSession;

public abstract class MongoRepository<TId, TAggregate extends Aggregate<TId>> implements Repository<TId, TAggregate> {

  protected MongoRepository(MongoSession session) {
    this.session = session;
  }

  @Override
  public TAggregate get(TId id) {
    return session.get(id, entityType());
  }

  @Override
  public void add(TAggregate entity) {
    session.save(entity);
  }

  @Override
  public void delete(TAggregate entity) {
    session.delete(entity);
  }

  protected Class<TAggregate> entityType() {
    return (Class<TAggregate>) typeToken.getRawType();
  }

  private final MongoSession session;
  private final TypeToken<TAggregate> typeToken = new TypeToken<TAggregate>(getClass()) {
  };
}
