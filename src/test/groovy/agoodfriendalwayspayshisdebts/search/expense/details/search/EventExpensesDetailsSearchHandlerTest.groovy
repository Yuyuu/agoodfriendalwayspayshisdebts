package agoodfriendalwayspayshisdebts.search.expense.details.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventExpensesDetailsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  EventExpensesDetailsSearchHandler handler = new EventExpensesDetailsSearchHandler()

  def "can return the expenses details of an event"() {
    given:
    jongo.collection("eventexpensesdetails_view") << [
        _id: eventId,
        expenseCount: 1,
        expenses: [[label: "label", purchaserName: "kim", amount: 2, participantsNames: ["kim"], description: "hello"]]
    ]

    when:
    def search = new EventExpensesDetailsSearch(eventId)
    search.skip(0).limit(5)
    def details = handler.execute(search, jongo.jongo())

    then:
    details.eventId == eventId
    details.expenseCount == 1
    details.expenses.size() == 1
    details.expenses[0].label == "label"
    details.expenses[0].purchaserName == "kim"
    details.expenses[0].amount == 2
    details.expenses[0].participantsNames == ["kim"]
    details.expenses[0].description == "hello"
  }

  def "can return the expenses progressively"() {
    given:
    jongo.collection("eventexpensesdetails_view") << [
        _id: eventId,
        expenseCount: 6,
        expenses: [[label: "e1"], [label: "e2"], [label: "e3"], [label: "e4"], [label: "e5"], [label: "e6"]]
    ]

    when:
    def search = new EventExpensesDetailsSearch(eventId)
    search.skip(skip).limit(limit)
    def details = handler.execute(search, jongo.jongo())

    then:
    details.expenseCount == 6
    details.expenses*.label == expenses

    where:
    skip | limit || expenses
    0    | 2     || ["e5", "e6"]
    2    | 2     || ["e3", "e4"]
    5    | 2     || ["e1"]
    5    | 5     || ["e1"]
    2    | 11    || ["e1", "e2", "e3", "e4"]
    2    | 5     || ["e1", "e2", "e3", "e4"]
    6    | 5     || []
    7    | 2     || []
  }

  def "returns null if no document is found for the event"() {
    when:
    def search = new EventExpensesDetailsSearch(eventId)
    def details = handler.execute(search, jongo.jongo())

    then:
    details == null
  }
}
