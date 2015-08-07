package com.vter.infrastructure.bus;

import com.google.common.reflect.TypeToken;

public interface MessageHandler<TCommand extends Message<TResponse>, TResponse> {

  TResponse execute(TCommand command);

  default Class<TCommand> commandType() {
    return (Class<TCommand>) new TypeToken<TCommand>(getClass()) {}.getRawType();
  }
}
