package agoodfriendalwayspayshisdebts.search.event.activity.search

import com.vter.search.WithJongo
import org.joda.time.DateTime
import org.junit.Rule
import spock.lang.Specification

class HistorySearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  HistorySearchHandler handler = new HistorySearchHandler()

  def "returns the history of an event"() {
    given:
    def operationId = UUID.randomUUID()
    jongo.collection("eventactivity_view") << [
        _id: operationId, type: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00"), data: "hello", eventId: eventId
    ]

    when:
    def search = new HistorySearch(eventId, "expenses")
    search.page(1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations.size() == 1
    operations[0].id == operationId
    operations[0].type == "NEW_EXPENSE"
    operations[0].creationDate.year == 2010
    operations[0].data == "hello"
    operations[0].eventId == eventId
  }

  def "can return the history of expenses progressively"() {
    given:
    populateView()

    when:
    def search = new HistorySearch(eventId, "expenses")
    search.page(page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["10", "7", "4"]
    2    || ["1"]
    3    || []
  }

  def "can return the history of participants"() {
    given:
    populateView()

    when:
    def search = new HistorySearch(eventId, "participants")
    search.page(page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["8", "3"]
    2    || []
  }

  def "can return the history of reminders"() {
    given:
    populateView()

    when:
    def search = new HistorySearch(eventId, "reminders")
    search.page(page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["9", "6", "2"]
    2    || []
  }

  def "only retrieves activity of the given event"() {
    given:
    jongo.collection("eventactivity_view") << [
        [data: "5", eventId: eventId, type: "NEW_EXPENSE"], [data: "4", eventId: UUID.randomUUID(), type: "NEW_EXPENSE"],
        [data: "3", eventId: eventId, type: "NEW_EXPENSE"], [data: "2", eventId: UUID.randomUUID(), type: "NEW_EXPENSE"],
        [data: "1", eventId: eventId, type: "NEW_EXPENSE"]
    ].each {
      it["_id"] = UUID.randomUUID()
    }

    when:
    def search = new HistorySearch(eventId, "expenses")
    search.page(1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == ["5", "3", "1"]
  }

  private void populateView() {
    jongo.collection("eventactivity_view") << [
        [data: "4", type: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00")], [data: "8", type: "NEW_PARTICIPANT", creationDate: date("2010-06-30T08:00")],
        [data: "1", type: "EXPENSE_DELETED", creationDate: date("2010-06-30T01:00")], [data: "9", type: "NEW_REMINDER", creationDate: date("2010-06-30T09:00")],
        [data: "6", type: "NEW_REMINDER", creationDate: date("2010-06-30T06:00")], [data: "3", type: "PARTICIPANT_EDITED", creationDate: date("2010-06-30T03:00")],
        [data: "10", type: "NEW_EXPENSE", creationDate: date("2010-06-30T10:00")], [data: "7", type: "EXPENSE_DELETED", creationDate: date("2010-06-30T07:00")],
        [data: "2", type: "NEW_REMINDER", creationDate: date("2010-06-30T02:00")], [data: "5", type: "EVENT_CREATION", creationDate: date("2010-06-30T05:00")]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }
  }

  private static long date(String dateAsString) {
    DateTime.parse(dateAsString).millis
  }
}
