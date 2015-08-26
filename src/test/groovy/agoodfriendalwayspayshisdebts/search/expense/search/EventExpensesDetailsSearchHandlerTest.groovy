package agoodfriendalwayspayshisdebts.search.expense.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventExpensesDetailsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID kimId = UUID.randomUUID()
  UUID eventId = UUID.randomUUID()

  EventExpensesDetailsSearchHandler handler = new EventExpensesDetailsSearchHandler()

  def "can return the expenses details of an event"() {
    given:
    jongo.collection("eventexpensesdetails_view") << [
        _id: eventId,
        expenses: [[label: "label", purchaserId: kimId, amount: 2, participantsIds: [kimId], description: "hello"]]
    ]

    when:
    def eventResult = handler.execute(new EventExpensesDetailsSearch(eventId), jongo.jongo())

    then:
    eventResult.eventId == eventId
    eventResult.expenses.size() == 1
    eventResult.expenses[0].label == "label"
    eventResult.expenses[0].purchaserId == kimId
    eventResult.expenses[0].amount == 2
    eventResult.expenses[0].participantsIds == [kimId] as Set
    eventResult.expenses[0].description == "hello"
  }
}
