package com.vter.model.internal_event;

public final class InternalEventBusLocator {

  private InternalEventBusLocator() {}

  public static InternalEventBus INSTANCE() {
    return instance;
  }

  public static void initialize(InternalEventBus instance) {
    InternalEventBusLocator.instance = instance;
  }

  private static InternalEventBus instance;
}
