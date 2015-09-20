package agoodfriendalwayspayshisdebts.command.email

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class SendReminderCommandTest extends Specification {
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  def "a null or empty list of participants uuids is a violation"() {
    given:
    def command = new SendReminderCommand(recipientsUuids: recipientsUuids)

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1

    where:
    recipientsUuids << [[], null]
  }
}
