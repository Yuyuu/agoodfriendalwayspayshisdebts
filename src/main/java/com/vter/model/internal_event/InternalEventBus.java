package com.vter.model.internal_event;

public interface InternalEventBus {

  void publish(InternalEvent event);

  static InternalEventBus INSTANCE() {
    return InternalEventBusLocator.INSTANCE();
  }
}
