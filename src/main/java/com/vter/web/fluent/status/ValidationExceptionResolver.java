package com.vter.web.fluent.status;

import com.vter.command.ValidationException;
import net.codestory.http.constants.HttpStatus;

public class ValidationExceptionResolver implements ExceptionResolver {

  @Override
  public boolean isAbleToResolve(Throwable throwable) {
    return ValidationException.class.isAssignableFrom(throwable.getClass());
  }

  @Override
  public int status() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    assert isAbleToResolve(throwable);
    ValidationException exception = (ValidationException) throwable;
    return ErrorRepresentation.fromErrorMessages(exception.messages());
  }
}
