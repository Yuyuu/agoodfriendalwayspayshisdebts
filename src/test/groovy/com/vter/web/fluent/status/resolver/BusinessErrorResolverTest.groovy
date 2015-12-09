package com.vter.web.fluent.status.resolver

import com.vter.model.BusinessError
import spock.lang.Specification

class BusinessErrorResolverTest extends Specification {
  BusinessErrorResolver resolver = new BusinessErrorResolver()

  def "can resolve business errors"() {
    expect:
    resolver.canResolve(new FakeError(""))
  }

  def "a business error is a bad request"() {
    expect:
    resolver.status() == 400
  }

  def "represents the error in json"() {
    given:
    def error = new FakeError("This is an error")

    when:
    def representation = resolver.representation(error)

    then:
    representation.errors() == [[message: "This is an error"]]
  }

  private static class FakeError extends BusinessError {
    FakeError(String message) {
      super(message)
    }
  }
}
