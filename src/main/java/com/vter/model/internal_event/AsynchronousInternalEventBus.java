package com.vter.model.internal_event;

import com.google.common.collect.Lists;
import com.vter.command.CommandSynchronization;
import com.vter.infrastructure.bus.AsynchronousBus;

import javax.inject.Inject;
import java.util.Queue;
import java.util.Set;

public class AsynchronousInternalEventBus extends AsynchronousBus implements InternalEventBus, CommandSynchronization {

  @Inject
  public AsynchronousInternalEventBus(Set<InternalEventSynchronization> synchronizations, Set<InternalEventHandler> handlers) {
    super(synchronizations, handlers);
  }

  @Override
  public void publish(InternalEvent event) {
    events.get().add(event);
  }

  @Override
  public void afterExecution() {
    LOGGER.debug("Propagating events");
    while (!events.get().isEmpty()) {
      final InternalEvent event = events.get().poll();
      assert event != null;
      sendToBus(event);
    }
  }

  @Override
  public void onError() {
    events.get().clear();
  }

  private void sendToBus(InternalEvent event) {
    if (isSynchronous(event)) {
      sendAndWaitResponse(event);
    } else {
      send(event);
    }
  }

  private static boolean isSynchronous(InternalEvent event) {
    return event.getClass().getAnnotation(Synchronous.class) != null;
  }

  private final ThreadLocal<Queue<InternalEvent>> events = ThreadLocal.withInitial(Lists::newLinkedList);
}
