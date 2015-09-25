package agoodfriendalwayspayshisdebts.search.expense.search

import spock.lang.Specification

class EventExpensesMetadataSearchHandlerTest extends Specification {

  EventExpensesMetadataSearchHandler handler

  def setup() {
    handler = new EventExpensesMetadataSearchHandler()
  }

  def "returns a fake iterable of expenses metadata"() {
    expect:
    handler.execute(new EventExpensesMetadataSearch(null)).size() == 3
  }
}
