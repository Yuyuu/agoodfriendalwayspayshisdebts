package com.vter.infrastructure.persistence.mongo;

import org.mongolink.MongoSession;

public interface MongoSessionProvider {

  MongoSession currentSession();
}
