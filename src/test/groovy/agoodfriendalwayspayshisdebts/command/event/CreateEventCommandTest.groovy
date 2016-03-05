package agoodfriendalwayspayshisdebts.command.event

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class CreateEventCommandTest extends Specification {
  @Shared
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  @Shared
  Validator validator = validatorFactory.validator

  @Unroll
  def "name with value {#name} is a violation"() {
    given:
    def command = new CreateEventCommand(name: name, currency: "€", participants: [[name: "kim", share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "EVENT_NAME_REQUIRED"

    where:
    name << [null, "  "]
  }

  def "requires a currency"() {
    given:
    def command = new CreateEventCommand(name: "event", participants: [[name: "kim", share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "EVENT_CURRENCY_REQUIRED"
  }

  def "requires at least one participant"() {
    given:
    def command = new CreateEventCommand(name: "event", currency: "€", participants: participants)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "PARTICIPANTS_REQUIRED"

    where:
    participants << [null, []]
  }
}
