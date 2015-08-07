package com.vter.infrastructure.bus;

public interface BusSynchronization {

  default void beforeExecution(Message<?> message) {}

  default void onError() {}

  default void afterExecution() {}

  default void ultimately() {}
}
