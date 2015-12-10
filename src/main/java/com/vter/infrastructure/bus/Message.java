package com.vter.infrastructure.bus;

public interface Message<TResponse> {

  default boolean requiresNewMongoSession() {
    return true;
  }
}
