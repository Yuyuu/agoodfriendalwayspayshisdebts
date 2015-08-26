package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails
import spock.lang.Specification

class ExpenseDetailsTest extends Specification {

  def "can create from an expense"() {
    given:
    def purchaserId = UUID.randomUUID()
    def participantsIds = [purchaserId]
    def expense = new Expense("label", purchaserId, 10, participantsIds)
    expense.description = "description"

    when:
    def expenseDetails = ExpenseDetails.fromExpense(expense, [(purchaserId): "ben"])

    then:
    expenseDetails.label == "label"
    expenseDetails.purchaserName == "ben"
    expenseDetails.amount == 10
    expenseDetails.participantsNames == ["ben"]
    expenseDetails.description == "description"
  }
}
