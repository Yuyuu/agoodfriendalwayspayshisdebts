package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.expense.Expense
import spock.lang.Specification

class ExpenseDetailsTest extends Specification {

  def "can create from an expense"() {
    given:
    def purchaserId = UUID.randomUUID()
    def participantsIds = [purchaserId]
    def expense = new Expense("label", purchaserId, 10, participantsIds)
    expense.description = "description"

    when:
    def expenseDetails = ExpenseDetails.fromExpense(expense)

    then:
    expenseDetails.label == "label"
    expenseDetails.purchaserId == purchaserId
    expenseDetails.amount == 10
    expenseDetails.participantsIds == participantsIds as Set
    expenseDetails.description == "description"
  }
}
