package com.vter.infrastructure.persistence.mongo;

import com.vter.model.EntityWithUuid;
import com.vter.model.RepositoryWithUuid;
import org.mongolink.MongoSession;

import java.util.UUID;

public abstract class MongoLinkRepositoryWithUuid<TEntity extends EntityWithUuid> extends MongoLinkRepository<UUID, TEntity> implements RepositoryWithUuid<TEntity> {

  protected MongoLinkRepositoryWithUuid(MongoSession session) {
    super(session);
  }
}
