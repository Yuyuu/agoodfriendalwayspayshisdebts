package agoodfriendalwayspayshisdebts.search.expense.metadata.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "updates the expenses metadata"() {
    given:
    jongo.collection("expensesmetadata_view") << [_id: eventId, metadata: [[id: null, label: "e1"]]]

    when:
    def expense = new Expense("e2", null, 1, [], eventId)
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(expense))

    then:
    def document = jongo.collection("expensesmetadata_view").findOne()
    document["metadata"].size() == 2
    def expenseMetadataDocument = document["metadata"][1]
    expenseMetadataDocument["id"] == expense.id()
    expenseMetadataDocument["label"] == expense.label()
  }

  def "creates the expenses metadata if it does not exist yet"() {
    when:
    def expense = new Expense("e1", null, 1, [], eventId)
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(expense))

    then:
    def document = jongo.collection("expensesmetadata_view").findOne()
    document["_id"] == eventId
    def expenseMetadataDocument = document["metadata"][0]
    expenseMetadataDocument["id"] == expense.id()
    expenseMetadataDocument["label"] == expense.label()
  }
}
