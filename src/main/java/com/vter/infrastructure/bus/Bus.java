package com.vter.infrastructure.bus;

import java.util.concurrent.CompletableFuture;

public interface Bus {

  <TResponse> CompletableFuture<ExecutionResult<TResponse>> send(Message<TResponse> message);

  <TResponse> ExecutionResult<TResponse> sendAndWaitResponse(Message<TResponse> message);
}
