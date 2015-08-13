package com.vter.web.fluent.status.resolver

import net.codestory.http.errors.NotFoundException
import spock.lang.Specification

class NotFoundExceptionResolverTest extends Specification {
  NotFoundExceptionResolver resolver = new NotFoundExceptionResolver()

  def "can resolve not found exceptions"() {
    expect:
    resolver.isAbleToResolve(new NotFoundException())
  }

  def "a not found exception is a 404 error"() {
    expect:
    resolver.status() == 404
  }

  def "has no json representation"() {
    expect:
    resolver.representation(new NotFoundException()) == null
  }
}
