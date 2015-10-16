package agoodfriendalwayspayshisdebts.command.event

import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class UpdateParticipantCommandTest extends Specification {
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  @Unroll
  def "name with value {#name} is a violation"() {
    given:
    def command = new UpdateParticipantCommand(name: name)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "PARTICIPANT_NAME_REQUIRED"

    where:
    name << [null, "  "]
  }

  def "an invalid email is a violation"() {
    given:
    def command = new UpdateParticipantCommand(name: "kim", email: "meh")

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "INVALID_EMAIL"
  }
}
