package agoodfriendalwayspayshisdebts.model.expense

import spock.lang.Specification

class UnknownExpenseResolverTest extends Specification {
  UnknownExpenseResolver resolver = new UnknownExpenseResolver()

  def "an unknown expense exception is a 404 error"() {
    expect:
    resolver.status() == 404
  }

  def "has no json representation"() {
    expect:
    resolver.representation(new UnknownExpense()) == null
  }
}
