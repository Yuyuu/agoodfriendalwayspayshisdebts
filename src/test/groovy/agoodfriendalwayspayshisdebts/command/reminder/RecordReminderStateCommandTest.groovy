package agoodfriendalwayspayshisdebts.command.reminder

import spock.lang.Shared
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class RecordReminderStateCommandTest extends Specification {
  @Shared
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  @Shared
  Validator validator = validatorFactory.validator

  def "a null eventId is a violation"() {
    given:
    def command = new RecordReminderStateCommand(event: "dropped", participantId: UUID.randomUUID())

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "a null participantId is a violation"() {
    given:
    def command = new RecordReminderStateCommand(event: "dropped", eventId: UUID.randomUUID())

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1
  }

  def "the event has to be delivered or dropped"() {
    given:
    def command = new RecordReminderStateCommand(event: event, eventId: UUID.randomUUID(), participantId: UUID.randomUUID())

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == violationCount

    where:
    event       || violationCount
    "dropped"   || 0
    "delivered" || 0
    "opened"    || 1
    "clicked"   || 1
  }
}
