package com.vter.infrastructure.persistence.mongo

import com.vter.model.Repository
import org.mongolink.MongoSession

class FakeAggregateRepository extends MongoRepository<String, FakeAggregate> implements Repository<String, FakeAggregate> {

  protected FakeAggregateRepository(MongoSession session) {
    super(session)
  }
}
