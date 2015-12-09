package com.vter.web.fluent.status.resolver;

import com.google.common.reflect.TypeToken;
import com.vter.web.fluent.status.ErrorRepresentation;

public interface ExceptionResolver<TException extends RuntimeException> {

  default boolean canResolve(Throwable throwable) {
    final Class<TException> resolverSkill = (Class<TException>) new TypeToken<TException>(getClass()) {}.getRawType();
    return (throwable != null) && (resolverSkill.isAssignableFrom(throwable.getClass()));
  }

  int status();

  default ErrorRepresentation representation(Throwable throwable) {
    return null;
  }
}
