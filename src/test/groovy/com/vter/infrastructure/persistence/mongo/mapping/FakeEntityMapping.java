package com.vter.infrastructure.persistence.mongo.mapping;

import com.vter.infrastructure.persistence.mongo.FakeEntity;
import org.mongolink.domain.mapper.AggregateMap;

@SuppressWarnings("unused")
public class FakeEntityMapping extends AggregateMap<FakeEntity> {

  @Override
  public void map() {
    id().onProperty(FakeEntity::getId).natural();
  }
}
