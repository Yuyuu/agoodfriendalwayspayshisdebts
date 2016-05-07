package agoodfriendalwayspayshisdebts.search.expense.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import agoodfriendalwayspayshisdebts.model.expense.State
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseDeletedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  OnExpenseDeleted handler

  def setup() {
    handler = new OnExpenseDeleted(jongo.jongo())
  }

  def "updates the state of the expense to deleted"() {
    given:
    def expense = new Expense("", null, 1, [], eventId)
    jongo.collection("expensesdetails_view") << [_id: eventId, totalCount: 1, items: [[id:expense.id, state:"ADDED"]]]

    when:
    expense.state(State.DELETED)
    handler.executeInternalEvent(new ExpenseDeletedInternalEvent(expense))

    then:
    def document = jongo.collection("expensesdetails_view").findOne()
    document["totalCount"] == 1
    document["items"][0]["state"] == "DELETED"
  }
}
