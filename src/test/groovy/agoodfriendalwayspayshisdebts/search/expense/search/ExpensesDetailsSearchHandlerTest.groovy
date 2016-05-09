package agoodfriendalwayspayshisdebts.search.expense.search

import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseMetadata
import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesSearchResult
import com.vter.search.WithJongo
import org.joda.time.DateTime
import org.junit.Rule
import spock.lang.Specification

class ExpensesDetailsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = uuid()

  ExpensesDetailsSearchHandler handler = new ExpensesDetailsSearchHandler()

  def "can return the expenses details of an event"() {
    given:
    jongo.collection("expensesdetails_view") << [
        [_id: uuid(), eventId: eventId, label: "label", purchaserName: "kim", amount: 2, participantsNames: ["kim"],
         description: "hello"]
    ]

    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(5).page(1)
    def result = handler.execute(search, jongo.jongo())

    then:
    result instanceof ExpensesSearchResult<ExpenseDetails>
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
        [label: "e1", creationDate: date("2010-06-30T01:00")], [label: "e2", creationDate: date("2010-06-30T02:00")],
        [label: "e3", creationDate: date("2010-06-30T03:00")], [label: "e4", creationDate: date("2010-06-30T04:00")],
        [label: "e5", creationDate: date("2010-06-30T05:00")], [label: "e6", creationDate: date("2010-06-30T06:00")]
    ].eachWithIndex { it, index ->
      it["_id"] = uuid()
      it["eventId"] = eventId
      it["state"] = index % 2 == 0 ? "DELETED" : "ADDED"
    }

    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(perPage).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result.totalCount == 6
    result.items*.label == expenses

    where:
    page | perPage || expenses
    1    | 2       || ["e6", "e5"]
    2    | 2       || ["e4", "e3"]
    3    | 2       || ["e2", "e1"]
    4    | 2       || []
    2    | 5       || ["e1"]
    1    | 11      || ["e6", "e5", "e4", "e3", "e2", "e1"]
    3    | 11      || []
  }

  def "can return the metadata format of expenses"() {
    given:
    jongo.collection("expensesdetails_view") << [
        [label: "e1", creationDate: date("2010-06-30T01:00")], [label: "e2", creationDate: date("2010-06-30T02:00")],
        [label: "e3", creationDate: date("2010-06-30T03:00")], [label: "e4", creationDate: date("2010-06-30T04:00")],
        [label: "e5", creationDate: date("2010-06-30T05:00")], [label: "e6", creationDate: date("2010-06-30T06:00")]
    ].eachWithIndex { it, index ->
      it["_id"] = uuid()
      it["eventId"] = eventId
      it["state"] = index % 2 == 0 ? "DELETED" : "ADDED"
    }

    when:
    def search = new ExpensesDetailsSearch(eventId, "meta").perPage(perPage).page(page)
    def result = handler.execute(search, jongo.jongo())

    then:
    result instanceof ExpensesSearchResult<ExpenseMetadata>
    result.totalCount == 3
    result.items*.label == expenses

    where:
    page | perPage || expenses
    1    | 2       || ["e6", "e4"]
    2    | 2       || ["e2"]
    3    | 2       || []
  }

  def "returns a result if no document is found for the event"() {
    when:
    def search = new ExpensesDetailsSearch(eventId, "").perPage(1).page(1)
    def details = handler.execute(search, jongo.jongo())

    then:
    details.totalCount == 0
    details.items.empty
  }

  private static UUID uuid() {
    UUID.randomUUID()
  }

  private static long date(String dateAsString) {
    DateTime.parse(dateAsString).millis
  }
}
