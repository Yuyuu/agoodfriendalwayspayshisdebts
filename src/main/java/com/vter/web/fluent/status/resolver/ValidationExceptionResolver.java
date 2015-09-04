package com.vter.web.fluent.status.resolver;

import com.vter.command.ValidationException;
import com.vter.web.fluent.status.ErrorRepresentation;
import net.codestory.http.constants.HttpStatus;

public class ValidationExceptionResolver implements ExceptionResolver<ValidationException> {

  @Override
  public int status() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    assert isAbleToResolve(throwable);
    final ValidationException exception = (ValidationException) throwable;
    return ErrorRepresentation.fromErrorMessages(exception.messages());
  }
}
