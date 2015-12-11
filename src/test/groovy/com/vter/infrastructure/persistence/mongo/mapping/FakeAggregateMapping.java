package com.vter.infrastructure.persistence.mongo.mapping;

import com.vter.infrastructure.persistence.mongo.FakeAggregate;
import org.mongolink.domain.mapper.AggregateMap;

@SuppressWarnings("unused")
public class FakeAggregateMapping extends AggregateMap<FakeAggregate> {

  @Override
  public void map() {
    id().onProperty(FakeAggregate::getId).natural();
  }
}
