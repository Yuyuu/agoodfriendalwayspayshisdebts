package com.vter.web.fluent.status.resolver

import com.vter.search.PaginationError
import spock.lang.Specification

class PaginationErrorResolverTest extends Specification {
  PaginationErrorResolver resolver = new PaginationErrorResolver()

  def "can resolve pagination errors"() {
    expect:
    resolver.canResolve(new PaginationError())
  }

  def "a pagination error is a bad request"() {
    expect:
    resolver.status() == 400
  }

  def "represents the error in json"() {
    given:
    def error = new PaginationError()

    when:
    def representation = resolver.representation(error)

    then:
    representation.errors() == [[message: "Bad pagination request"]]
  }
}
