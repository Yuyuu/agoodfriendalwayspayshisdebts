package com.vter.model.internal_event;

import com.vter.infrastructure.bus.Message;

public interface InternalEvent extends Message<Void> {

  @Override
  default boolean requiresNewMongoSession() {
    return getClass().getAnnotation(Synchronous.class) == null;
  }
}
