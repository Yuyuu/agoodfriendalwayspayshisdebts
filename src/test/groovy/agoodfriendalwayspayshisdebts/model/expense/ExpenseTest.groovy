package agoodfriendalwayspayshisdebts.model.expense

import spock.lang.Specification

class ExpenseTest extends Specification {

  def "can create an expense with a label, a purchaser id, an amount and ids of the participants"() {
    given:
    def purchaserId = uuid()
    def expense = new Expense("food", purchaserId, 10, [purchaserId])

    expect:
    expense.label() == "food"
    expense.purchaserId() == purchaserId
    expense.amount() == 10
    expense.participantsIds().first() == purchaserId
  }

  def "is shared between at most each participant once"() {
    given:
    def purchaserId = uuid()
    def expense = new Expense("food", purchaserId, 10, [purchaserId])

    when:
    expense.participantsIds().add(purchaserId)

    then:
    expense.participantsIds().size() == 1
  }

  def "has an optional description"() {
    given:
    def expense = new Expense("food", uuid(), 10, [])

    when:
    expense.description = "a description"

    then:
    expense.description() == "a description"
  }

  def uuid() {
    return UUID.randomUUID()
  }
}
