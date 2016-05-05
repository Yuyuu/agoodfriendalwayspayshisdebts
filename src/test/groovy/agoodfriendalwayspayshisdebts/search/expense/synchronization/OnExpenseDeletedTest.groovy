package agoodfriendalwayspayshisdebts.search.expense.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
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

  def "removes the expense from the collection"() {
    given:
    def expense = new Expense("", null, 1, [], eventId)
    jongo.collection("expensesdetails_view") << [_id: eventId, expenseCount: 1, expenses: [[id:expense.id]]]

    when:
    handler.executeInternalEvent(new ExpenseDeletedInternalEvent(expense))

    then:
    def document = jongo.collection("expensesdetails_view").findOne()
    document["expenseCount"] == 0
    document["expenses"].size() == 0
  }
}
