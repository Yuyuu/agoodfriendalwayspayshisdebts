package com.vter.infrastructure.bus;

import com.google.common.util.concurrent.ListenableFuture;

public interface Bus {

  <TResponse> ListenableFuture<ExecutionResult<TResponse>> send(Message<TResponse> message);

  <TResponse> ExecutionResult<TResponse> sendAndWaitResponse(Message<TResponse> message);
}
