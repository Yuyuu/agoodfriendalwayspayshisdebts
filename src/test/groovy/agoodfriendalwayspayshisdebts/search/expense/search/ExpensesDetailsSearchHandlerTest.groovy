package agoodfriendalwayspayshisdebts.search.expense.search

import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesMetadata
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class ExpensesDetailsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  ExpensesDetailsSearchHandler handler = new ExpensesDetailsSearchHandler()

  def "can return the expenses details of an event"() {
    given:
    jongo.collection("expensesdetails_view") << [
        _id: eventId,
        totalCount: 1,
        items: [[label: "label", purchaserName: "kim", amount: 2, participantsNames: ["kim"], description: "hello"]]
    ]

    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(5).page(1)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.eventId == eventId
    result.totalCount == 1
    result.items.size() == 1
    result.items[0].label == "label"
    result.items[0].purchaserName == "kim"
    result.items[0].amount == 2
    result.items[0].participantsNames == ["kim"]
    result.items[0].description == "hello"
  }

  def "can return the expenses progressively"() {
    given:
    jongo.collection("expensesdetails_view") << [
        _id: eventId,
        totalCount: 6,
        items: [[label: "e1"], [label: "e2"], [label: "e3"], [label: "e4"], [label: "e5"], [label: "e6"]]
    ]

    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(perPage).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.totalCount == 6
    result.items*.label == expenses

    where:
    page | perPage || expenses
    1    | 2       || ["e5", "e6"]
    2    | 2       || ["e3", "e4"]
    3    | 2       || ["e1", "e2"]
    4    | 2       || []
    2    | 5       || ["e1"]
    1    | 11      || ["e1", "e2", "e3", "e4", "e5", "e6"]
    3    | 11      || []
  }

  def "can return the metadata format of expenses"() {
    given:
    jongo.collection("expensesdetails_view") << [
        _id: eventId,
        totalCount: 6,
        items: [[label: "e1"], [label: "e2"], [label: "e3"], [label: "e4"], [label: "e5"], [label: "e6"]]
    ]

    when:
    def search = new ExpensesDetailsSearch(eventId, "meta").perPage(perPage).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result instanceof ExpensesMetadata
    result.totalCount == 6
    result.items*.label == expenses

    where:
    page | perPage || expenses
    1    | 2       || ["e5", "e6"]
    2    | 2       || ["e3", "e4"]
    3    | 2       || ["e1", "e2"]
  }

  def "returns null if no document is found for the event"() {
    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(1).page(1)
    def details = handler.execute(search, jongo.jongo())

    then:
    details == null
  }
}
