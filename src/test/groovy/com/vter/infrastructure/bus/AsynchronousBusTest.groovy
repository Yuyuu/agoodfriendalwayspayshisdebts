package com.vter.infrastructure.bus

import com.google.common.collect.Sets
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Specification

import java.util.concurrent.ExecutorService

class AsynchronousBusTest extends Specification {

  def "can execute a command"() {
    given:
    def handler = new FakeCommandHandler()
    def bus = busWith(handler)
    def command = new FakeMessage()

    when:
    bus.send(command)

    then:
    handler.commandReceived == command
  }

  def "is asynchronous"() {
    given:
    def executor = Mock(ExecutorService)
    def bus = bus()
    bus.setExecutor(executor)

    when:
    bus.send(new FakeMessage())

    then:
    1 * executor.execute({ o -> o != null })
  }

  def "encapsulates the commands into the synchronizations"() {
    given:
    def synchronization = Mock(BusSynchronization)
    def bus = busWith(synchronization)
    def command = new FakeMessage()

    when:
    bus.send(command)

    then:
    1 * synchronization.beforeExecution(command)
    then:
    1 * synchronization.afterExecution()
    then:
    1 * synchronization.ultimately()
  }

  def "still calls the synchronization on error"() {
    given:
    def handler = new FakeCommandHandler()
    handler.returnException()
    def synchronisationBus = Mock(BusSynchronization)
    def bus = busWith(synchronisationBus, handler)

    when:
    bus.send(new FakeMessage())

    then:
    1 * synchronisationBus.onError()
    then:
    1 * synchronisationBus.ultimately()
  }

  def "returns the result of a command"() {
    given:
    def handler = new FakeCommandHandler()
    def bus = busWith(handler)

    when:
    final ListenableFuture<ExecutionResult<String>> promise = bus.send(new FakeMessage())

    then:
    promise != null
    final ExecutionResult<String> response = Futures.getUnchecked(promise)
    response.success
    response.data() == "Winter is coming"
  }

  def "can directly return the result"() {
    given:
    def handler = new FakeCommandHandler()
    def bus = busWith(handler)

    when:
    def result = bus.sendAndWaitResponse(new FakeMessage())

    then:
    result != null
  }

  def "returns a result on error"() {
    setup:
    def handler = new FakeCommandHandler()
    handler.returnException();
    def bus = busWith(handler);

    when:
    final ListenableFuture<ExecutionResult<String>> promise = bus.send(new FakeMessage())

    then:
    promise != null
    final ExecutionResult<String> response = Futures.getUnchecked(promise)
    !response.success
    response.error() instanceof RuntimeException
    response.error().message == "This is an error"
  }

  def "returns an error if there is no handler for the given message"() {
    given:
    def bus = anEmptyBus()

    when:
    def promise = bus.send(new FakeMessage())

    then:
    promise != null
    def executionResult = promise.get()
    !executionResult.success
    executionResult.error() instanceof BusError
  }

  def anEmptyBus() {
    new AsynchronousBus(Sets.newHashSet(), Sets.newHashSet()) {}
  }

  private AsynchronousBus bus() {
    new AsynchronousBus(Sets.newHashSet(Mock(BusSynchronization)), Sets.newHashSet(new FakeCommandHandler())) {
    };
  }

  private AsynchronousBus busWith(BusSynchronization synchronisationBus, FakeCommandHandler handler) {
    final AsynchronousBus bus = new AsynchronousBus(Sets.newHashSet(synchronisationBus), Sets.newHashSet(handler)) {
    }
    bus.setExecutor(executor())
    return bus;
  }

  private static ExecutorService executor() {
    return MoreExecutors.newDirectExecutorService();
  }

  private AsynchronousBus busWith(BusSynchronization synchronization) {
    return busWith(synchronization, new FakeCommandHandler())
  }

  private AsynchronousBus busWith(FakeCommandHandler handler) {
    busWith(Mock(BusSynchronization), handler)
  }

  private class FakeMessage implements Message<String> {

    private FakeMessage() {}
  }

  private class FakeCommandHandler implements MessageHandler<FakeMessage, String> {

    @Override
    public String execute(FakeMessage command) {
      commandReceived = command;
      if (exception) {
        throw new RuntimeException("This is an error");
      }
      return "Winter is coming";
    }

    public void returnException() {
      this.exception = true;
    }

    private FakeMessage commandReceived;
    private boolean exception;
  }
}
