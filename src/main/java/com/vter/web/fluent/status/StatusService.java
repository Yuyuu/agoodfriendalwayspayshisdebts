package com.vter.web.fluent.status;

public interface StatusService {

  int getStatus(Throwable throwable);

  ErrorRepresentation getRepresentation(Throwable throwable);
}
