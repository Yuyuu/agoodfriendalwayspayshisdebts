package agoodfriendalwayspayshisdebts.search.event.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {

  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "can update the details of the event"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("event", [kim])
    def expense = new Expense("label", kim.id(), 4, [kim.id()])
    event.expenses().add(expense)
    RepositoryLocator.events().save(event)

    and:
    jongo.collection("eventdetails_view") << [
        _id: event.id, name: event.name(),
        participants: [[id: kim.id(), name: kim.name(), share: kim.share(), email: kim.email()]],
        expenses: []
    ]

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    // TODO: For some reason (and since jongo 1.2) when checking with GMongo the list is transformed into a map
    // TODO: (i.e. [0:[...], 1:[...]]) after jongo update in test environment. Therefore the deserialized object is used instead.
    def expenseFromDocument = jongo.jongo().getCollection("eventdetails_view").findOne().as(EventDetails.class).expenses[0]
    expenseFromDocument.label == "label"
    expenseFromDocument.purchaserId == kim.id()
    expenseFromDocument.amount == 4
    expenseFromDocument.participantsIds[0] == kim.id()
  }
}
