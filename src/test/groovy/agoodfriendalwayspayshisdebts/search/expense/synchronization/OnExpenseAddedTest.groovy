package agoodfriendalwayspayshisdebts.search.expense.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "can update the expenses details of the event"() {
    given:
    def kimId = UUID.randomUUID()
    def eventId = UUID.randomUUID()

    and:
    jongo.collection("eventexpensesdetails_view") << [
        _id: eventId,
        expenses: [[label: "label", purchaserId: kimId, amount: 2, participantsIds: [kimId], description: "hello"]]
    ]

    when:
    def expense = new Expense("hey", kimId, 4, [kimId])
    handler.executeEvent(new ExpenseAddedInternalEvent(eventId, expense))

    then:
    // TODO: For some reason (and since jongo 1.2) when checking with GMongo the list is transformed into a map
    // TODO: (i.e. [0:[...], 1:[...]]) after jongo update in test environment. Therefore the deserialized object is used instead.
    def details = jongo.jongo().getCollection("eventexpensesdetails_view").findOne().as(EventExpensesDetails.class)
    details.eventId == eventId
    details.expenses.size() == 2
    details.expenses[1].label == "hey"
    details.expenses[1].purchaserId == kimId
    details.expenses[1].amount == 4
    details.expenses[1].participantsIds == [kimId] as Set
    details.expenses[1].description == null
  }

  def "creates the expenses details if it does not exist yet"() {
    given:
    def kimId = UUID.randomUUID()
    def eventId = UUID.randomUUID()

    when:
    def expense = new Expense("hey", kimId, 4, [kimId])
    handler.executeEvent(new ExpenseAddedInternalEvent(eventId, expense))

    then:
    def document = jongo.collection("eventexpensesdetails_view").findOne()
    document["_id"] == eventId
    document["expenses"].size() == 1
  }
}
