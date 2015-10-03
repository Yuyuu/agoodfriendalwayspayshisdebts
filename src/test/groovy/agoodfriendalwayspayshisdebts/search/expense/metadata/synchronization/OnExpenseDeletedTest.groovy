package agoodfriendalwayspayshisdebts.search.expense.metadata.synchronization

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

  def "deletes the expense metadata from the collection"() {
    given:
    def expense = new Expense("", null, 1, [])
    jongo.collection("expensesmetadata_view") << [_id: eventId, metadata: [[id:expense.id()]]]

    when:
    handler.executeInternalEvent(new ExpenseDeletedInternalEvent(eventId, expense))

    then:
    def document = jongo.collection("expensesmetadata_view").findOne()
    document["metadata"].size() == 0
  }
}
