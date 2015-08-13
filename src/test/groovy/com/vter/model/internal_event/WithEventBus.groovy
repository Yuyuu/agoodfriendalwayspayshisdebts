package com.vter.model.internal_event

import org.junit.rules.ExternalResource

class WithEventBus extends ExternalResource {

  @Override
  protected void before() throws Throwable {
    bus = new FakeInternalEventBus()
    InternalEventBusLocator.initialize(bus)
  }

  @Override
  protected void after() {
    InternalEventBusLocator.initialize(null)
  }

  public FakeInternalEventBus getBus() {
    return bus
  }

  private FakeInternalEventBus bus
}
