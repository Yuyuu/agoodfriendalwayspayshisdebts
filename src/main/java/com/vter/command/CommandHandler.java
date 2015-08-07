package com.vter.command;

import com.vter.infrastructure.bus.MessageHandler;

public interface CommandHandler<TCommand extends Command<TResponse>, TResponse> extends MessageHandler<TCommand, TResponse> {
}
