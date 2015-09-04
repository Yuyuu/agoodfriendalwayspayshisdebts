package com.vter.web.fluent.status;

import com.google.common.collect.Lists;
import com.vter.web.fluent.status.resolver.ExceptionResolver;
import com.vter.web.fluent.status.resolver.NotFoundExceptionResolver;
import com.vter.web.fluent.status.resolver.UnknownExpenseResolver;
import com.vter.web.fluent.status.resolver.ValidationExceptionResolver;
import net.codestory.http.constants.HttpStatus;

import java.util.List;
import java.util.Optional;

public class ApplicationStatusService implements StatusService {

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
    return resolvers.stream().filter(resolver -> resolver.isAbleToResolve(throwable)).findFirst();
  }

  private List<ExceptionResolver> resolvers = Lists.newArrayList(
      new NotFoundExceptionResolver(),
      new ValidationExceptionResolver(),
      new UnknownExpenseResolver()
  );
}
