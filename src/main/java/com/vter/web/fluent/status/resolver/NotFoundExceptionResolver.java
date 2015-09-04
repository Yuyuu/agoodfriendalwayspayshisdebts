package com.vter.web.fluent.status.resolver;

import net.codestory.http.constants.HttpStatus;
import net.codestory.http.errors.NotFoundException;

public class NotFoundExceptionResolver implements ExceptionResolver<NotFoundException> {

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }
}
