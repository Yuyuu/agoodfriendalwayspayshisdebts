package com.vter.model.internal_event

import spock.lang.Specification

class InternalEventTest extends Specification {

  def "an asynchronous internal event requires a new mongo session"() {
    expect:
    new FakeAsynchronousInternalEvent().requiresNewMongoSession()
  }

  def "a synchronous internal event does not require a new mongo session"() {
    expect:
    ! new FakeSynchronousInternalEvent().requiresNewMongoSession()
  }

  @Synchronous
  private static class FakeSynchronousInternalEvent implements InternalEvent {
  }

  private static class FakeAsynchronousInternalEvent implements InternalEvent {
  }
}
