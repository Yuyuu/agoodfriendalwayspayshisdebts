package com.vter.infrastructure.bus;

public class ExecutionResult<TResponse> {

  public static <TResponse> ExecutionResult<TResponse> success(TResponse response) {
    return new ExecutionResult<>(response, true);
  }

  public static <TResponse> ExecutionResult<TResponse> error(Throwable error) {
    return new ExecutionResult<>(error);
  }

  private ExecutionResult(TResponse response, boolean success) {
    this.response = response;
    this.success = success;
    this.error = null;
  }

  private ExecutionResult(Throwable error) {
    this.error = error;
    success = false;
    response = null;
  }

  public TResponse data() {
    return response;
  }

  public boolean isSuccess() {
    return success;
  }

  public Throwable error() {
    return error;
  }

  private final TResponse response;
  private final boolean success;
  private final Throwable error;
}
