package agoodfriendalwayspayshisdebts.search.event.activity.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventActivitySearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  EventActivitySearchHandler handler = new EventActivitySearchHandler()

  def "returns the activity of an event"() {
    given:
    def operationId = UUID.randomUUID()
    jongo.collection("eventactivity_view") << [
        _id: eventId,
        operations: [[id: operationId.toString(), type: "NEW_EXPENSE", creationDate: "2015-11-12", data: "hello", eventId: eventId.toString()]]
    ]

    when:
    def search = new EventActivitySearch(eventId)
    search.page(1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations.size() == 1
    operations[0].id == operationId.toString()
    operations[0].type == "NEW_EXPENSE"
    operations[0].creationDate == "2015-11-12"
    operations[0].data == "hello"
    operations[0].eventId == eventId.toString()
  }

  def "can return the activity progressively"() {
    given:
    jongo.collection("eventactivity_view") << [
        _id: eventId,
        operations: [
            [data: "22"], [data: "21"], [data: "20"], [data: "19"], [data: "18"], [data: "17"], [data: "16"],
            [data: "15"], [data: "14"], [data: "13"], [data: "12"], [data: "11"], [data: "10"], [data: "9"],
            [data: "8"], [data: "7"], [data: "6"], [data: "5"], [data: "4"], [data: "3"], [data: "2"], [data: "1"]
        ]
    ]

    when:
    def search = new EventActivitySearch(eventId)
    search.page(page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expenses

    where:
    page || expenses
    1    || ["22", "21", "20", "19", "18", "17", "16", "15", "14", "13"]
    3    || ["2", "1"]
    4    || []
  }

  def "returns null if no document is found for the event"() {
    when:
    def search = new EventActivitySearch(eventId)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations == null
  }
}
