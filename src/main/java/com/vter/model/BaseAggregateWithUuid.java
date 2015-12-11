package com.vter.model;

import java.util.UUID;

public abstract class BaseAggregateWithUuid extends BaseAggregate<UUID> implements AggregateWithUuid {

  protected BaseAggregateWithUuid() {
    super(UUID.randomUUID());
  }

  protected BaseAggregateWithUuid(UUID uuid) {
    super(uuid);
  }
}
