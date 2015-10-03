package agoodfriendalwayspayshisdebts.search.expense.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.search.expense.details.model.EventExpensesDetails
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Participant kim = new Participant("kim", 1, null)
  Event event = new Event("event", [kim])

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
    RepositoryLocator.events().save(event)
  }

  def "can update the expenses details of the event"() {
    given:
    jongo.collection("eventexpensesdetails_view") << [
        _id: event.id,
        expenseCount: 1,
        expenses: [[label: "label", purchaserName: kim.name(), amount: 2, participantsNames: [kim.name()], description: "hello"]]
    ]

    when:
    def expense = new Expense("hey", kim.id(), 4, [kim.id()])
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    // TODO: For some reason (and since jongo 1.2) when checking with GMongo the list is transformed into a map
    // TODO: (i.e. [0:[...], 1:[...]]) after jongo update in test environment. Therefore the deserialized object is used instead.
    def details = jongo.jongo().getCollection("eventexpensesdetails_view").findOne().as(EventExpensesDetails.class)
    details.eventId == event.id
    details.expenseCount == 2
    details.expenses.size() == 2
    details.expenses[1].id == expense.id()
    details.expenses[1].label == "hey"
    details.expenses[1].purchaserName == "kim"
    details.expenses[1].amount == 4
    details.expenses[1].participantsNames == ["kim"]
    details.expenses[1].description == null
  }

  def "creates the expenses details if it does not exist yet"() {
    when:
    def expense = new Expense("hey", kim.id(), 4, [kim.id()])
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    def document = jongo.collection("eventexpensesdetails_view").findOne()
    document["_id"] == event.id
    document["expenses"].size() == 1
  }
}
