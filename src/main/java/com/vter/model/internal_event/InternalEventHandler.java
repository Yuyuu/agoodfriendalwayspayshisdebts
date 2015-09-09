package com.vter.model.internal_event;

import com.vter.infrastructure.bus.MessageHandler;

public interface InternalEventHandler<TInternalEvent extends InternalEvent> extends MessageHandler<TInternalEvent, Void> {

  void executeInternalEvent(TInternalEvent internalEvent);

  default Void execute(TInternalEvent internalEvent) {
    executeInternalEvent(internalEvent);
    return null;
  }
}
