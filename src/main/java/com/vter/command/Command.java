package com.vter.command;

import com.vter.infrastructure.bus.Message;

public interface Command<TResponse> extends Message<TResponse> {
}
