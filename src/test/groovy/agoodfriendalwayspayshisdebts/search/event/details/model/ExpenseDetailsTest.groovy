package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails
import spock.lang.Specification

class ExpenseDetailsTest extends Specification {

  def "can create for an expense"() {
    given:
    def purchaserId = UUID.randomUUID()
    def participantsIds = [purchaserId]
    def eventId = UUID.randomUUID()
    def expense = new Expense("label", purchaserId, 10, participantsIds, eventId)
    expense.description = "description"

    when:
    def expenseDetails = ExpenseDetails.forExpense(expense, [(purchaserId): "ben"])

    then:
    expenseDetails.id == expense.id
    expenseDetails.label == "label"
    expenseDetails.state == "PENDING"
    expenseDetails.purchaserName == "ben"
    expenseDetails.amount == 10
    expenseDetails.participantsNames == ["ben"]
    expenseDetails.creationDate == expense.creationDate()
    expenseDetails.description == "description"
    expenseDetails.eventId == eventId
  }
}
