package com.vter.infrastructure.persistence.mongo;

import com.vter.model.AggregateWithUuid;
import com.vter.model.RepositoryWithUuid;
import org.mongolink.MongoSession;

import java.util.UUID;

public abstract class MongoRepositoryWithUuid<TAggregate extends AggregateWithUuid> extends MongoRepository<UUID, TAggregate> implements RepositoryWithUuid<TAggregate> {

  protected MongoRepositoryWithUuid(MongoSession session) {
    super(session);
  }
}
