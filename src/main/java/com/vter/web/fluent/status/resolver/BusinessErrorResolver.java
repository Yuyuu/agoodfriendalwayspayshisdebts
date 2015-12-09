package com.vter.web.fluent.status.resolver;

import com.google.common.collect.Lists;
import com.vter.model.BusinessError;
import com.vter.web.fluent.status.ErrorRepresentation;
import net.codestory.http.constants.HttpStatus;

public class BusinessErrorResolver implements ExceptionResolver<BusinessError> {
  @Override
  public int status() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorRepresentation representation(Throwable throwable) {
    final BusinessError error = (BusinessError) throwable;
    return ErrorRepresentation.forErrorMessages(Lists.newArrayList(error.getMessage()));
  }
}
