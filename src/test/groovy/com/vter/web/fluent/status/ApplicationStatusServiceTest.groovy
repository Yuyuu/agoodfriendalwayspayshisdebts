package com.vter.web.fluent.status

import com.vter.command.ValidationException
import com.vter.web.fluent.status.resolver.ValidationExceptionResolver
import spock.lang.Specification

class ApplicationStatusServiceTest extends Specification {

  ApplicationStatusService statusService = new ApplicationStatusService([new ValidationExceptionResolver()] as Set)

  def "can ask the resolver for a status"() {
    expect:
    statusService.getStatus(new ValidationException([])) == 400
  }

  def "has a default status for unresolved errors"() {
    expect:
    statusService.getStatus(new FakeException()) == 500
  }

  def "can ask the resolver for a representation of the error"() {
    expect:
    statusService.getRepresentation(new ValidationException([])) != null
  }

  def "an error with no resolver has no representation"() {
    expect:
    statusService.getRepresentation(new FakeException()) == null
  }

  static class FakeException extends RuntimeException {
    public FakeException() {}
  }
}
