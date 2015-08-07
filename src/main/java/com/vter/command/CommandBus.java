package com.vter.command;

import com.vter.infrastructure.bus.AsynchronousBus;

import javax.inject.Inject;
import java.util.Set;

public class CommandBus extends AsynchronousBus {

  @Inject
  public CommandBus(Set<CommandSynchronization> synchronizations, Set<CommandHandler> handlers) {
    super(synchronizations, handlers);
  }
}
