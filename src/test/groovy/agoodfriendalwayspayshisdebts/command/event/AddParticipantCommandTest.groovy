package agoodfriendalwayspayshisdebts.command.event

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class AddParticipantCommandTest extends Specification {
  @Shared ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  @Shared Validator validator = validatorFactory.validator

  @Unroll
  def "name with value {#name} is a violation"() {
    given:
    def command = new AddParticipantCommand(name: name, email: "email@mail.com", share: 1)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "PARTICIPANT_NAME_REQUIRED"

    where:
    name << [null, "  "]
  }

  def "an invalid email address is a violation"() {
    given:
    def command = new AddParticipantCommand(name: "name", email: "email", share: 1)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "INVALID_EMAIL"
  }

  def "a null share is a violation"() {
    given:
    def command = new AddParticipantCommand(name: "name", email: "email@mail.com", share: null)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "PARTICIPANT_SHARE_REQUIRED"
  }

  def "a share smaller than 1 is a violation"() {
    given:
    def command = new AddParticipantCommand(name: "name", email: "email@mail.com", share: 0)

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "INVALID_SHARE"
  }
}
