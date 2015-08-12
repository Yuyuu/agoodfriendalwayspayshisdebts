package com.vter.model.internal_event

import spock.lang.Specification

import java.util.concurrent.ExecutorService

@SuppressWarnings("GroovyAccessibility")
class AsynchronousInternalEventBusTest extends Specification {
  def bus = new AsynchronousInternalEventBus([] as Set, [new FakeEventHandler()] as Set)

  def "can publish an event"() {
    when:
    bus.publish(Mock(InternalEvent))

    then:
    bus.events.get().size() == 1
  }

  def "clears all the internal events on error"() {
    given:
    bus.publish(Mock(InternalEvent))

    when:
    bus.onError()

    then:
    bus.events.get().size() == 0
  }

  def "sends all the stored events to the bus after execution"() {
    given:
    2.times {
      bus.publish(new FakeEvent())
    }
    def executor = Mock(ExecutorService)
    bus.executor = executor

    when:
    bus.afterExecution()

    then:
    2 * executor.execute({it -> it != null})
  }

  private static class FakeEvent implements InternalEvent {
    private FakeEvent() {}
  }

  private static class FakeEventHandler implements InternalEventHandler<FakeEvent> {
    @Override
    void executeEvent(FakeEvent event) {}
  }
}
