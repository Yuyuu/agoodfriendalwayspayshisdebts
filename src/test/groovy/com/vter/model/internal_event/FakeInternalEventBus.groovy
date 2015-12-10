package com.vter.model.internal_event

class FakeInternalEventBus implements InternalEventBus {

  @Override
  void publish(InternalEvent... internalEvents) {
    internalEvents.each { events << [(it.getClass()): it] }
  }

  public <T extends InternalEvent> T lastEvent(final Class<T> type) {
    return (T) events.get(type);
  }

  private Map<Class<? extends InternalEvent>, InternalEvent> events = [:]
}
