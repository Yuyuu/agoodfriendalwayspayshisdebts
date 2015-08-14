package agoodfriendalwayspayshisdebts.command.expense

import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class AddExpenseCommandTest extends Specification {
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  @Unroll
  def "label with value {#label} is a violation"() {
    given:
    def command = new AddExpenseCommand(
        label: label,
        purchaserUuid: "uuid",
        amount: 1
    )

    when:
    def violations = validator.validate(command)

    then:
    violations[0].message == "EXPENSE_LABEL_REQUIRED"

    where:
    label << [null, "  "]
  }

  @Unroll
  def "purchaserUuid with value {#purchaserUuid} is a violation"() {
    given:
    def command = new AddExpenseCommand(
        label: "label",
        purchaserUuid: purchaserUuid,
        amount: 1
    )

    when:
    def violations = validator.validate(command)

    then:
    violations[0].message == "EXPENSE_PURCHASER_REQUIRED"

    where:
    purchaserUuid << [null, "  "]
  }

  def "a null amount is a violation"() {
    given:
    def command = new AddExpenseCommand(
        label: "label",
        purchaserUuid: "uuid",
        amount: null
    )

    when:
    def violations = validator.validate(command)

    then:
    violations[0].message == "EXPENSE_AMOUNT_REQUIRED"
  }

  def "a negative amount is a violation"() {
    given:
    def command = new AddExpenseCommand(
        label: "label",
        purchaserUuid: "uuid",
        amount: -1
    )

    when:
    def violations = validator.validate(command)

    then:
    violations[0].message == "INVALID_AMOUNT"
  }
}
