package com.vter.web.actions;

import com.google.common.base.Throwables;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.payload.Payload;

import java.util.Optional;
import java.util.UUID;

public abstract class BaseAction {

  protected Payload getIdPayloadOrFail(ExecutionResult<UUID> executionResult, int responseStatus) {
    final IdHolder idHolder = new IdHolder(getDataOrFail(executionResult));
    return new Payload(idHolder).withCode(responseStatus);
  }

  protected <T> Payload getDataAsPayloadOrFail(ExecutionResult<T> executionResult, int responseStatus) {
    return new Payload(getDataOrFail(executionResult)).withCode(responseStatus);
  }

  protected <T> T getDataOrFail(ExecutionResult<T> executionResult) {
    if (executionResult == null) {
      return null;
    }
    if (!executionResult.isSuccess()) {
      Throwables.propagate(executionResult.error());
      return null;
    }
    return executionResult.data();
  }

  protected <T> Optional<T> getOptionalDataOrFail(ExecutionResult<T> executionResult) {
    return Optional.ofNullable(getDataOrFail(executionResult));
  }
}
