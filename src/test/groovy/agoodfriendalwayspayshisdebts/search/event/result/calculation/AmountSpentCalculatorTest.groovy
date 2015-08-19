package agoodfriendalwayspayshisdebts.search.event.result.calculation

import agoodfriendalwayspayshisdebts.model.expense.Expense
import spock.lang.Specification

class AmountSpentCalculatorTest extends Specification {
  AmountSpentCalculator calculator

  def "calculates the total amount spent by each participant"() {
    given:
    def kimId = UUID.randomUUID()
    def benId = UUID.randomUUID()
    def expenses = [anExpense(kimId, 2.5), anExpense(kimId, 4.78), anExpense(benId, 10), anExpense(benId, 0.30)]

    when:
    calculator = new AmountSpentCalculator(expenses)
    def result = calculator.calculate()

    then:
    result[kimId] == 7.28D
    result[benId] == 10.3D
  }

  def anExpense(UUID purchaserId, double amount) {
    return new Expense(null, purchaserId, amount, [])
  }
}
