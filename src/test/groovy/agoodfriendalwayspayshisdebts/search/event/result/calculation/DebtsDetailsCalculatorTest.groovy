package agoodfriendalwayspayshisdebts.search.event.result.calculation

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import org.junit.Assert
import spock.lang.Specification

class DebtsDetailsCalculatorTest extends Specification {
  def kim = new Participant("kim", 1, null)
  def ben = new Participant("ben", 1, null)
  def leaAndBob = new Participant("lea & bob", 2, null)

  DebtsDetailsCalculator calculator

  def "calculates the debt towards each participant"() {
    given:
    def expenses = [
        anExpenseOf(kim).withCost(12).sharedBetween(kim, ben, leaAndBob),
        anExpenseOf(kim).withCost(6.44).sharedBetween(kim, ben),
        anExpenseOf(ben).withCost(9.99).sharedBetween(leaAndBob, ben)
    ]

    when:
    calculator = new DebtsDetailsCalculator(expenses, [kim, ben, leaAndBob])
    def details = calculator.calculate()

    then:
    isAlmostEqual(0D, details[kim.id()].debtTowards(ben))
    isAlmostEqual(0D, details[kim.id()].debtTowards(leaAndBob))
    isAlmostEqual(6.22D, details[ben.id()].debtTowards(kim))
    isAlmostEqual(0D, details[ben.id()].debtTowards(leaAndBob))
    isAlmostEqual(6D, details[leaAndBob.id()].debtTowards(kim))
    isAlmostEqual(6.66D, details[leaAndBob.id()].debtTowards(ben))
  }

  def "mitigates the debt between participants"() {
    given:
    def expenses = [
        anExpenseOf(kim).withCost(12).sharedBetween(kim, ben, leaAndBob),
        anExpenseOf(leaAndBob).withCost(4.44).sharedBetween(kim, leaAndBob, ben),
        anExpenseOf(ben).withCost(3.93).sharedBetween(leaAndBob, ben)
    ]

    when:
    calculator = new DebtsDetailsCalculator(expenses, [kim, ben, leaAndBob])
    def details = calculator.calculate()

    then:
    isAlmostEqual(0D, details[kim.id()].debtTowards(ben))
    isAlmostEqual(0D, details[kim.id()].debtTowards(leaAndBob))
    isAlmostEqual(3D, details[ben.id()].debtTowards(kim))
    isAlmostEqual(0D, details[ben.id()].debtTowards(leaAndBob))
    isAlmostEqual(4.89D, details[leaAndBob.id()].debtTowards(kim))
    isAlmostEqual(1.51D, details[leaAndBob.id()].debtTowards(ben))
  }

  def "calculates the total mitigated debt of participants"() {
    given:
    def expenses = [
        anExpenseOf(kim).withCost(12).sharedBetween(kim, ben, leaAndBob),
        anExpenseOf(leaAndBob).withCost(8.88).sharedBetween(kim, leaAndBob, ben),
        anExpenseOf(ben).withCost(6.66).sharedBetween(leaAndBob, ben)
    ]

    when:
    calculator = new DebtsDetailsCalculator(expenses, [kim, ben, leaAndBob])
    def details = calculator.calculate()

    then:
    isAlmostEqual(0D, details[kim.id()].totalDebt())
    isAlmostEqual(3D, details[ben.id()].totalDebt())
    isAlmostEqual(6D, details[leaAndBob.id()].totalDebt())
  }

  private static ExpenseBuilder anExpenseOf(Participant participant) {
    return new ExpenseBuilder(purchaserId: participant.id())
  }

  private static class ExpenseBuilder {
    UUID purchaserId
    double amount

    ExpenseBuilder withCost(double amount) {
      this.amount = amount
      return this
    }

    Expense sharedBetween(Participant... participants) {
      def participantsIds = participants.collect { it.id() }
      return new Expense(null, purchaserId, amount, participantsIds)
    }
  }

  private static void isAlmostEqual(double a, double b) {
    Assert.assertEquals(a, b, 0.000001)
  }
}
