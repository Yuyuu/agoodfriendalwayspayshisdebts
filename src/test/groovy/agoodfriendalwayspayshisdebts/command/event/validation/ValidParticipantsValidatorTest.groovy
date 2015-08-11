package agoodfriendalwayspayshisdebts.command.event.validation

import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ValidParticipantsValidatorTest extends Specification {

  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  Validator validator = validatorFactory.validator

  def "no participants is not a violation"() {
    given:
    def command = new FakeCommand(participants: participants)

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 0

    where:
    participants << [null, []]
  }

  def "validates participants with a name and a share"() {
    given:
    def command = new FakeCommand(participants: [[name: "kim", share: 1], [name: "bob", share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 0
  }

  def "a participant without a name is a violation"() {
    given:
    def command = new FakeCommand(participants: [[share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  @Unroll
  def "a participant with name {#name} is a violation"() {
    given:
    def command = new FakeCommand(participants: [[name: name, share: 1]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == numberOfViolations

    where:
    name || numberOfViolations
    null || 1
    "  " || 1
  }

  def "a participant without a share is a violation"() {
    given:
    def command = new FakeCommand(participants: [[name: "kim"]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "a participant with a share smaller than 1 is a violation"() {
    given:
    def command = new FakeCommand(participants: [[name: "kim", share: 0]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "finds all the violations"() {
    given:
    def command = new FakeCommand(participants: [[name: "kim", share: 0], [name: "  "]])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 3
    violations.count { it -> it.message == "PARTICIPANT_NAME_REQUIRED" } == 1
    violations.count { it -> it.message == "PARTICIPANT_SHARE_REQUIRED" } == 1
    violations.count { it -> it.message == "INVALID_SHARE" } == 1
  }

  private class FakeCommand {
    @ValidParticipants
    List<Map<String, Object>> participants
  }
}
