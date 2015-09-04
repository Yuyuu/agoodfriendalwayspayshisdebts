package com.vter.web.fluent.status.resolver

import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense
import spock.lang.Specification

class UnknownExpenseResolverTest extends Specification {
  UnknownExpenseResolver resolver = new UnknownExpenseResolver()

  def "can resolve unknown expense exceptions"() {
    expect:
    resolver.isAbleToResolve(new UnknownExpense())
  }

  def "an unknown expense exception is a 404 error"() {
    expect:
    resolver.status() == 404
  }

  def "has no json representation"() {
    expect:
    resolver.representation(new UnknownExpense()) == null
  }
}
