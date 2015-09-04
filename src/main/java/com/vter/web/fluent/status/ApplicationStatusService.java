package com.vter.web.fluent.status;

import com.google.common.collect.Maps;
import com.vter.web.fluent.status.resolver.ExceptionResolver;
import net.codestory.http.constants.HttpStatus;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ApplicationStatusService implements StatusService {

  @Inject
  public ApplicationStatusService(Set<ExceptionResolver> resolvers) {
    resolvers.forEach(resolver -> this.resolvers.put(resolver.exceptionType(), resolver));
  }

  @Override
  public int getStatus(Throwable throwable) {
    final Optional<ExceptionResolver> resolver = resolver(throwable);
    if (resolver.isPresent()) {
      return resolver.get().status();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public ErrorRepresentation getRepresentation(Throwable throwable) {
    final Optional<ExceptionResolver> resolver = resolver(throwable);
    if (resolver.isPresent()) {
      return resolver.get().representation(throwable);
    }
    return null;
  }

  private Optional<ExceptionResolver> resolver(Throwable throwable) {
    return Optional.ofNullable(resolvers.get(throwable.getClass()));
  }

  private Map<Class<?>, ExceptionResolver> resolvers = Maps.newHashMap();
}
