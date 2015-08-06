package com.vter.infrastructure.persistence.mongo

import com.vter.model.Repository
import org.mongolink.MongoSession

class FakeEntityRepository extends MongoLinkRepository<String, FakeEntity> implements Repository<String, FakeEntity> {

  protected FakeEntityRepository(MongoSession session) {
    super(session)
  }
}
