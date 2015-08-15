package agoodfriendalwayspayshisdebts.search.event.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
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
    def documentExpense = jongo.collection("eventdetails_view").findOne()["expenses"][0]
    documentExpense["label"] == "label"
    documentExpense["purchaserId"] == kim.id()
    documentExpense["amount"] == 4
    documentExpense["participantsIds"][0] == kim.id()
  }

  def "can create the event details if it does not exist"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("event", [kim])
    def expense = new Expense("label", kim.id(), 4, [kim.id()])
    event.expenses().add(expense)
    RepositoryLocator.events().save(event)

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    def document = jongo.collection("eventdetails_view").findOne()
    document["_id"] == event.id
    document["name"] == event.name()
    document["participants"].size() == 1

    and:
    def documentExpense = document["expenses"][0]
    documentExpense["label"] == "label"
    documentExpense["purchaserId"] == kim.id()
    documentExpense["amount"] == 4
    documentExpense["participantsIds"][0] == kim.id()
  }
}
