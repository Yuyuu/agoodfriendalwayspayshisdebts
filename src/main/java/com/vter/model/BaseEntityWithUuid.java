package com.vter.model;

import java.util.UUID;

public abstract class BaseEntityWithUuid extends BaseEntity<UUID> implements EntityWithUuid {

  public BaseEntityWithUuid() {
    super(UUID.randomUUID());
  }
}
