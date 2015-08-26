package agoodfriendalwayspayshisdebts.search.expense.search

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
        expenses: [[label: "label", purchaserName: "kim", amount: 2, participantsNames: ["kim"], description: "hello"]]
    ]

    when:
    def eventResult = handler.execute(new EventExpensesDetailsSearch(eventId), jongo.jongo())

    then:
    eventResult.eventId == eventId
    eventResult.expenses.size() == 1
    eventResult.expenses[0].label == "label"
    eventResult.expenses[0].purchaserName == "kim"
    eventResult.expenses[0].amount == 2
    eventResult.expenses[0].participantsNames == ["kim"]
    eventResult.expenses[0].description == "hello"
  }
}
