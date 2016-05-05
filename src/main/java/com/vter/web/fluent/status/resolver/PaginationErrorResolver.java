package com.vter.web.fluent.status.resolver;

import com.google.common.collect.Lists;
import com.vter.search.PaginationError;
import com.vter.web.fluent.status.ErrorRepresentation;

public class PaginationErrorResolver implements ExceptionResolver<PaginationError> {
  @Override
  public int status() {
    return 400;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    final PaginationError error = (PaginationError) throwable;
    return ErrorRepresentation.forErrorMessages(Lists.newArrayList(error.getMessage()));
  }
}
