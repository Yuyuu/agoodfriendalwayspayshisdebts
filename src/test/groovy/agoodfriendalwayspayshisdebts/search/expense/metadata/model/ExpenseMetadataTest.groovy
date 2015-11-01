package agoodfriendalwayspayshisdebts.search.expense.metadata.model

import agoodfriendalwayspayshisdebts.model.expense.Expense
import spock.lang.Specification

class ExpenseMetadataTest extends Specification {

  def "creates for an expense"() {
    given:
    def expense = new Expense("hello", null, 3, [], null)

    when:
    def expenseMetadata = ExpenseMetadata.forExpense(expense)

    then:
    expenseMetadata.id == expense.id()
    expenseMetadata.label == expense.label()
  }
}
