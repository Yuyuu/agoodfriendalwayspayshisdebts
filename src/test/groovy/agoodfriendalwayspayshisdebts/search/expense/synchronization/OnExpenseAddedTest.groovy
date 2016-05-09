package agoodfriendalwayspayshisdebts.search.expense.synchronization

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

  Participant kim = new Participant("kim", 1, null)
  Event event = new Event("event", "€", [kim])

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
    RepositoryLocator.events().add(event)
  }

  def "adds the new expense to the view"() {
    when:
    def expense = new Expense("hey", kim.id, 4, [kim.id], event.id)
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(expense))

    then:
    def details = jongo.collection("expensesdetails_view").findOne()
    details["_id"] == expense.id
    details["label"] == "hey"
    details["purchaserName"] == "kim"
    details["amount"] == 4
    details["participantsNames"] == ["kim"]
    details["creationDate"] == expense.creationDate().millis
    details["description"] == null
    details["eventId"] == event.id
  }
}
