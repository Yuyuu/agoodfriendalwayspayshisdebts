package com.vter.command;

import com.vter.web.fluent.status.ErrorRepresentation;
import com.vter.web.fluent.status.resolver.ExceptionResolver;
import net.codestory.http.constants.HttpStatus;

public class ValidationExceptionResolver implements ExceptionResolver<ValidationException> {

  @Override
  public int status() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    final ValidationException exception = (ValidationException) throwable;
    return ErrorRepresentation.forErrorMessages(exception.messages());
  }
}
