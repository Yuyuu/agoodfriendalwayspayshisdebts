package com.vter.web.fluent.status.resolver;

import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense;
import net.codestory.http.constants.HttpStatus;

public class UnknownExpenseResolver implements ExceptionResolver<UnknownExpense> {

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }
}
