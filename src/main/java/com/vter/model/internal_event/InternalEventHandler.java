package com.vter.model.internal_event;

import com.vter.infrastructure.bus.MessageHandler;

public interface InternalEventHandler<TEvent extends InternalEvent> extends MessageHandler<TEvent, Void> {

  void executeEvent(TEvent event);

  default Void execute(TEvent event) {
    executeEvent(event);
    return null;
  }
}
