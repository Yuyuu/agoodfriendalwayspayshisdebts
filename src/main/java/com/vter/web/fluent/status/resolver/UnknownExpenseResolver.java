package com.vter.web.fluent.status.resolver;

import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense;
import com.vter.web.fluent.status.ErrorRepresentation;
import net.codestory.http.constants.HttpStatus;

public class UnknownExpenseResolver implements ExceptionResolver {

  @Override
  public boolean isAbleToResolve(Throwable throwable) {
    return UnknownExpense.class.isAssignableFrom(throwable.getClass());
  }

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    return null;
  }
}
