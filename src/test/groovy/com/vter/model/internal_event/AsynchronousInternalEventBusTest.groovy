package com.vter.model.internal_event

import com.google.common.collect.Sets
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Specification

import java.util.concurrent.Callable

@SuppressWarnings("GroovyAccessibility")
class AsynchronousInternalEventBusTest extends Specification {

  def "can publish an event"() {
    when:
    def bus = busWith(new FakeEventHandler())
    bus.publish(Mock(InternalEvent))

    then:
    bus.events.get().size() == 1
  }

  def "does not execute the events right away"() {
    given:
    def handler = new FakeEventHandler()
    def bus = busWith(handler)

    when:
    bus.publish(new FakeEvent())

    then:
    !handler.called
  }

  def "executes the events after execution"() {
    given:
    def handler = new FakeEventHandler()
    def bus = busWith(handler)

    when:
    bus.publish(new FakeEvent())
    bus.afterExecution()

    then:
    handler.called
  }

  def "can execute an event synchronously"() {
    given:
    def bus = busWith(new FakeEventSynchronousHandler())
    def future = Mock(ListenableFuture)
    def executorService = Mock(ListeningExecutorService)
    executorService.submit(_ as Callable) >> future
    bus.syncExecutor = executorService

    when:
    bus.publish(new FakeEventSynchronous())
    bus.afterExecution()

    then:
    1 * future.get()
  }

  def "clears all the internal events on error"() {
    given:
    def bus = busWith(new FakeEventHandler())

    when:
    bus.publish(new FakeEvent())
    bus.onError()

    then:
    bus.events.get().size() == 0
  }

  private static AsynchronousInternalEventBus busWith(InternalEventHandler<? extends InternalEvent> handler) {
    def bus = new AsynchronousInternalEventBus(Sets.newHashSet(), Sets.newHashSet(handler))
    bus.setAsyncExecutor(MoreExecutors.newDirectExecutorService())
    return bus
  }

  private static class FakeEvent implements InternalEvent {
  }

  private static class FakeEventHandler implements InternalEventHandler<FakeEvent> {

    boolean called

    @Override
    void executeInternalEvent(FakeEvent internalEvent) {
      called = true
    }
  }

  @Synchronous
  private static class FakeEventSynchronous implements InternalEvent {
  }

  private static class FakeEventSynchronousHandler implements InternalEventHandler<FakeEventSynchronous> {

    boolean called

    @Override
    void executeInternalEvent(FakeEventSynchronous internalEvent) {
      called = true
    }
  }
}
