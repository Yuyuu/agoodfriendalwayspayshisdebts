package com.vter.web.fluent.status.resolver;

import com.vter.web.fluent.status.ErrorRepresentation;

public interface ExceptionResolver {

  boolean isAbleToResolve(Throwable throwable);

  int status();

  ErrorRepresentation representation(Throwable throwable);
}
