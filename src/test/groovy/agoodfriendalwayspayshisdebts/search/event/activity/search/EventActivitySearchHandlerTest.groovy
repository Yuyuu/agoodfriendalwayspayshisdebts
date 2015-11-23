package agoodfriendalwayspayshisdebts.search.event.activity.search

import com.vter.search.WithJongo
import org.joda.time.DateTime
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
        _id: operationId, type: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00"), data: "hello", eventId: eventId
    ]

    when:
    def search = new EventActivitySearch(eventId)
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

  def "can return the activity progressively"() {
    given:
    jongo.collection("eventactivity_view") << [
        [data: "22"], [data: "21"], [data: "20"], [data: "19"], [data: "18"], [data: "17"], [data: "16"], [data: "15"],
        [data: "14"], [data: "13"], [data: "12"], [data: "11"], [data: "10"], [data: "9"], [data: "8"],  [data: "7"],
        [data: "6"], [data: "5"], [data: "4"], [data: "3"], [data: "2"], [data: "1"]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }

    when:
    def search = new EventActivitySearch(eventId)
    search.page(page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["22", "21", "20", "19", "18", "17", "16", "15", "14", "13"]
    3    || ["2", "1"]
    4    || []
  }

  def "sorts the activity in descending order of creation date"() {
    given:
    jongo.collection("eventactivity_view") << [
        [data: "4", creationDate: date("2010-06-30T04:00")], [data: "9", creationDate: date("2010-06-30T09:00")],
        [data: "6", creationDate: date("2010-06-30T06:00")], [data: "10", creationDate: date("2010-06-30T10:00")],
        [data: "7", creationDate: date("2010-06-30T07:00")], [data: "1", creationDate: date("2010-06-30T01:00")],
        [data: "12", creationDate: date("2010-06-30T12:00")], [data: "2", creationDate: date("2010-06-30T02:00")],
        [data: "3", creationDate: date("2010-06-30T03:00")], [data: "5", creationDate: date("2010-06-30T05:00")],
        [data: "11", creationDate: date("2010-06-30T11:00")], [data: "8", creationDate: date("2010-06-30T08:00")]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }

    when:
    def search = new EventActivitySearch(eventId)
    search.page(1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == ["12", "11", "10", "9", "8", "7", "6", "5", "4", "3"]
  }

  def "only retrieves activity of the given event"() {
    given:
    jongo.collection("eventactivity_view") << [
        [data: "5", eventId: eventId], [data: "4", eventId: UUID.randomUUID()], [data: "3", eventId: eventId],
        [data: "2", eventId: UUID.randomUUID()], [data: "1", eventId: eventId]
    ].each {
      it["_id"] = UUID.randomUUID()
    }

    when:
    def search = new EventActivitySearch(eventId)
    search.page(1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == ["5", "3", "1"]
  }
  
  private static long date(String dateAsString) {
    DateTime.parse(dateAsString).millis
  }
}
