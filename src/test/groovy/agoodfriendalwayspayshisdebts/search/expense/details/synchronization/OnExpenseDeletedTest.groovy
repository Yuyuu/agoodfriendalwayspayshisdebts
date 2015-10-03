package agoodfriendalwayspayshisdebts.search.expense.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseDeletedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Event event = new Event("event", [])

  OnExpenseDeleted handler

  def setup() {
    handler = new OnExpenseDeleted(jongo.jongo())
    RepositoryLocator.events().save(event)
  }

  def "removes the expense from the collection"() {
    given:
    def expense = new Expense("", null, 1, [])
    jongo.collection("eventexpensesdetails_view") << [_id: event.id, expenseCount: 1, expenses: [[id:expense.id()]]]

    when:
    handler.executeInternalEvent(new ExpenseDeletedInternalEvent(event.id, expense))

    then:
    def document = jongo.collection("eventexpensesdetails_view").findOne()
    document["expenseCount"] == 0
    document["expenses"].size() == 0
  }
}
