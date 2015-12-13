package agoodfriendalwayspayshisdebts.search.event.activity.search

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter
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
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL, 1)
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
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL, page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["22", "21", "20", "19", "18", "17", "16", "15", "14", "13"]
    2    || ["12", "11", "10", "9", "8", "7", "6", "5", "4", "3"]
    3    || ["2", "1"]
    4    || []
  }

  def "sorts the activity in descending order of creation date"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL, 1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == ["15", "14", "13", "12", "11", "10", "9", "8", "7", "6"]
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
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL, 1)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == ["5", "3", "1"]
  }

  def "can return the activity that is only about expenses"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.EXPENSES, page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["12", "11", "10"]
    2    || ["7", "4", "1"]
    3    || []
  }

  def "can return the activity that is only about participants"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.PARTICIPANTS, page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["15", "13", "8"]
    2    || ["3"]
    3    || []
  }

  def "can return the activity that is only about reminders"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.REMINDERS, page)
    def operations = handler.execute(search, jongo.jongo())

    then:
    operations*.data == expected

    where:
    page || expected
    1    || ["14", "9", "6"]
    2    || ["2"]
    3    || []
  }

  private void populateView() {
    jongo.collection("eventactivity_view") << [
        [data: "4", type: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00")],
        [data: "8", type: "NEW_PARTICIPANT", creationDate: date("2010-06-30T08:00")],
        [data: "1", type: "EXPENSE_DELETED", creationDate: date("2010-06-30T01:00")],
        [data: "11", type: "EXPENSE_DELETED", creationDate: date("2010-06-30T11:00")],
        [data: "9", type: "REMINDER_DELIVERED", creationDate: date("2010-06-30T09:00")],
        [data: "6", type: "REMINDER_DROPPED", creationDate: date("2010-06-30T06:00")],
        [data: "3", type: "PARTICIPANT_EDITED", creationDate: date("2010-06-30T03:00")],
        [data: "10", type: "NEW_EXPENSE", creationDate: date("2010-06-30T10:00")],
        [data: "7", type: "EXPENSE_DELETED", creationDate: date("2010-06-30T07:00")],
        [data: "2", type: "REMINDER_DROPPED", creationDate: date("2010-06-30T02:00")],
        [data: "12", type: "NEW_EXPENSE", creationDate: date("2010-06-30T12:00")],
        [data: "5", type: "EVENT_CREATION", creationDate: date("2010-06-30T05:00")],
        [data: "14", type: "REMINDER_DELIVERED", creationDate: date("2010-06-30T14:00")],
        [data: "15", type: "NEW_PARTICIPANT", creationDate: date("2010-06-30T15:00")],
        [data: "13", type: "PARTICIPANT_EDITED", creationDate: date("2010-06-30T13:00")]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }
  }
  
  private static long date(String dateAsString) {
    DateTime.parse(dateAsString).millis
  }
}
