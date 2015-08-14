package agoodfriendalwayspayshisdebts.command.expense.validation

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class PositiveNonzeroAmountValidatorTest extends Specification {
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  def "no amount is not a violation"() {
    given:
    def command = new FakeCommand(amount: null)

    when:
    def violations = validator.validate(command)

    then:
    violations.empty
  }

  def "validates an amount greater than zero"() {
    given:
    def command = new FakeCommand(amount: 12.23)

    when:
    def violations = validator.validate(command)

    then:
    violations.empty
  }

  def "amount equal to zero is a violation"() {
    given:
    def command = new FakeCommand(amount: 0D)

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "negative amount is a violation"() {
    given:
    def command = new FakeCommand(amount: -1.24)

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  private static class FakeCommand {
    @PositiveNonzeroAmount
    Double amount
  }
}
