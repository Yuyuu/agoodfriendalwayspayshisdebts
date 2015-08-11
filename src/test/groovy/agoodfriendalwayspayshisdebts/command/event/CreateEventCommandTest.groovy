package agoodfriendalwayspayshisdebts.command.event

import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class CreateEventCommandTest extends Specification {

  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  @Unroll
  def "name with value {#name} is a violation"() {
    given:
    def command = new CreateEventCommand(name: name, participants: [[name: "kim", share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "EVENT_NAME_REQUIRED"

    where:
    name << [null, "  "]
  }

  def "requires at least one participant"() {
    given:
    def command = new CreateEventCommand(name: "event", participants: participants)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "PARTICIPANTS_REQUIRED"

    where:
    participants << [null, []]
  }
}
