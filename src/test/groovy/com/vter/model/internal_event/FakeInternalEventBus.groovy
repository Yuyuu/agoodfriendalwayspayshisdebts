package com.vter.model.internal_event

class FakeInternalEventBus implements InternalEventBus {

  @Override
  void publish(InternalEvent event) {
    events.put(event.class, event)
  }

  public <T extends InternalEvent> T lastEvent(final Class<T> type) {
    return (T) events.get(type);
  }

  private Map<Class<? extends InternalEvent>, InternalEvent> events = [:]
}
