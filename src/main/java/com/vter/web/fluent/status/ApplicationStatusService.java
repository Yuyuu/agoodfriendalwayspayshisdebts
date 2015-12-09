package com.vter.web.fluent.status;

import com.google.common.collect.Lists;
import com.vter.web.fluent.status.resolver.ExceptionResolver;
import net.codestory.http.constants.HttpStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ApplicationStatusService implements StatusService {

  @Inject
  public ApplicationStatusService(Set<ExceptionResolver> resolvers) {
    this.resolvers.addAll(resolvers);
  }

  @Override
  public int getStatus(Throwable throwable) {
    final Optional<ExceptionResolver> resolver = resolver(throwable);
    return resolver.isPresent() ? resolver.get().status() : HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public ErrorRepresentation getRepresentation(Throwable throwable) {
    final Optional<ExceptionResolver> resolver = resolver(throwable);
    return resolver.isPresent() ? resolver.get().representation(throwable) : null;
  }

  private Optional<ExceptionResolver> resolver(Throwable throwable) {
    return resolvers.stream().filter(resolver -> resolver.canResolve(throwable)).findFirst();
  }

  private List<ExceptionResolver> resolvers = Lists.newArrayList();
}
