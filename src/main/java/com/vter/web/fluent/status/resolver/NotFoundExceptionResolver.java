package com.vter.web.fluent.status.resolver;

import com.vter.web.fluent.status.ErrorRepresentation;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.errors.NotFoundException;

public class NotFoundExceptionResolver implements ExceptionResolver {

  @Override
  public boolean isAbleToResolve(Throwable throwable) {
    return NotFoundException.class.isAssignableFrom(throwable.getClass());
  }

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    return null;
  }
}
