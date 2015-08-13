package com.vter.web.fluent.status.resolver

import com.vter.command.ValidationException
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class ValidationExceptionResolverTest extends Specification {

  ValidationExceptionResolver resolver = new ValidationExceptionResolver()

  def "can resolve validation exceptions"() {
    expect:
    resolver.isAbleToResolve(new ValidationException([]))
  }

  def "a validation exception is a bad request"() {
    expect:
    resolver.status() == 400
  }

  def "represents the violations in json"() {
    given:
    def exception = new ValidationException(["hello", "hi"])

    when:
    def representation = resolver.representation(exception)

    then:
    representation.errors == [[message: "hello"], [message: "hi"]]
  }
}
