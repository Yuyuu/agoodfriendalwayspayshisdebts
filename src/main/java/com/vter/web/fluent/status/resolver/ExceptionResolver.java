package com.vter.web.fluent.status.resolver;

import com.google.common.reflect.TypeToken;
import com.vter.web.fluent.status.ErrorRepresentation;

public interface ExceptionResolver<TException extends RuntimeException> {

  default Class<TException> exceptionType() {
    return (Class<TException>) new TypeToken<TException>(getClass()) {}.getRawType();
  }

  int status();

  default ErrorRepresentation representation(Throwable throwable) {
    return null;
  }
}
