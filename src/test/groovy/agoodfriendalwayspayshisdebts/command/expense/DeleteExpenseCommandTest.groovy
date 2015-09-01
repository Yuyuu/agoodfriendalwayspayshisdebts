package agoodfriendalwayspayshisdebts.command.expense

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class DeleteExpenseCommandTest extends Specification {
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  def "a null event id is a violation"() {
    given:
    def command = new DeleteExpenseCommand(expenseId: UUID.randomUUID())

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "a null expense id is a violation"() {
    given:
    def command = new DeleteExpenseCommand(eventId: UUID.randomUUID())

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }
}
