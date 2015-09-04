package com.vter.web.fluent.status.resolver;

import com.google.common.reflect.TypeToken;
import com.vter.web.fluent.status.ErrorRepresentation;

public interface ExceptionResolver<TException extends RuntimeException> {

  default boolean isAbleToResolve(Throwable throwable) {
    return new TypeToken<TException>(getClass()) {}.getRawType().isAssignableFrom(throwable.getClass());
  }

  int status();

  default ErrorRepresentation representation(Throwable throwable) {
    return null;
  }
}
