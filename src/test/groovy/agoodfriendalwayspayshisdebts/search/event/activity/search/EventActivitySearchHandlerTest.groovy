package agoodfriendalwayspayshisdebts.search.event.activity.search

import agoodfriendalwayspayshisdebts.model.activity.OperationType
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
    jongo.collection("operationdetails_view") << [
        _id: operationId, operationType: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00"), operationData: "hello", eventId: eventId
    ]

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL).perPage(3).page(1)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.totalCount ==1
    result.items.size() == 1
    result.items[0].id == operationId
    result.items[0].operationType == OperationType.NEW_EXPENSE
    result.items[0].creationDate.year == 2010
    result.items[0].operationData == "hello"
    result.items[0].eventId == eventId
  }

  def "can return the activity progressively"() {
    given:
    jongo.collection("operationdetails_view") << [
        [operationData: "22"], [operationData: "21"], [operationData: "20"], [operationData: "19"], [operationData: "18"],
        [operationData: "17"], [operationData: "16"], [operationData: "15"], [operationData: "14"], [operationData: "13"],
        [operationData: "12"], [operationData: "11"], [operationData: "10"], [operationData: "9"], [operationData: "8"],
        [operationData: "7"], [operationData: "6"], [operationData: "5"], [operationData: "4"], [operationData: "3"],
        [operationData: "2"], [operationData: "1"]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL).perPage(10).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.totalCount == 22
    result.items*.operationData == expected

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
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL).perPage(10).page(1)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.items*.operationData == ["15", "14", "13", "12", "11", "10", "9", "8", "7", "6"]
  }

  def "only retrieves activity of the given event"() {
    given:
    jongo.collection("operationdetails_view") << [
        [operationData: "5", eventId: eventId], [operationData: "4", eventId: UUID.randomUUID()], [operationData: "3", eventId: eventId],
        [operationData: "2", eventId: UUID.randomUUID()], [operationData: "1", eventId: eventId]
    ].each {
      it["_id"] = UUID.randomUUID()
    }

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.ALL).perPage(10).page(1)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.items*.operationData == ["5", "3", "1"]
  }

  def "can return the activity that is only about expenses"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.EXPENSES).perPage(3).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.items*.operationData == expected

    where:
    page || expected
    1    || ["12", "11", "10"]
    2    || ["7", "4", "1"]
  }

  def "can return the activity that is only about participants"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.PARTICIPANTS).perPage(3).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.items*.operationData == expected

    where:
    page || expected
    1    || ["15", "13", "8"]
    2    || ["3"]
  }

  def "can return the activity that is only about reminders"() {
    given:
    populateView()

    when:
    def search = new EventActivitySearch(eventId, ActivityFilter.REMINDERS).perPage(3).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.items*.operationData == expected

    where:
    page || expected
    1    || ["14", "9", "6"]
    2    || ["2"]
  }

  private void populateView() {
    jongo.collection("operationdetails_view") << [
        [operationData: "4", operationType: "NEW_EXPENSE", creationDate: date("2010-06-30T04:00")],
        [operationData: "8", operationType: "NEW_PARTICIPANT", creationDate: date("2010-06-30T08:00")],
        [operationData: "1", operationType: "EXPENSE_DELETED", creationDate: date("2010-06-30T01:00")],
        [operationData: "11", operationType: "EXPENSE_DELETED", creationDate: date("2010-06-30T11:00")],
        [operationData: "9", operationType: "REMINDER_DELIVERED", creationDate: date("2010-06-30T09:00")],
        [operationData: "6", operationType: "REMINDER_DROPPED", creationDate: date("2010-06-30T06:00")],
        [operationData: "3", operationType: "PARTICIPANT_EDITED", creationDate: date("2010-06-30T03:00")],
        [operationData: "10", operationType: "NEW_EXPENSE", creationDate: date("2010-06-30T10:00")],
        [operationData: "7", operationType: "EXPENSE_DELETED", creationDate: date("2010-06-30T07:00")],
        [operationData: "2", operationType: "REMINDER_DROPPED", creationDate: date("2010-06-30T02:00")],
        [operationData: "12", operationType: "NEW_EXPENSE", creationDate: date("2010-06-30T12:00")],
        [operationData: "5", operationType: "EVENT_CREATION", creationDate: date("2010-06-30T05:00")],
        [operationData: "14", operationType: "REMINDER_DELIVERED", creationDate: date("2010-06-30T14:00")],
        [operationData: "15", operationType: "NEW_PARTICIPANT", creationDate: date("2010-06-30T15:00")],
        [operationData: "13", operationType: "PARTICIPANT_EDITED", creationDate: date("2010-06-30T13:00")]
    ].each {
      it["_id"] = UUID.randomUUID()
      it["eventId"] = eventId
    }
  }
  
  private static long date(String dateAsString) {
    DateTime.parse(dateAsString).millis
  }
}
