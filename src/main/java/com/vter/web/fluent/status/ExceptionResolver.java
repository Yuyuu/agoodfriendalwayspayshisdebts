package com.vter.web.fluent.status;

public interface ExceptionResolver {

  boolean isAbleToResolve(Throwable throwable);

  int status();

  ErrorRepresentation representation(Throwable throwable);
}
