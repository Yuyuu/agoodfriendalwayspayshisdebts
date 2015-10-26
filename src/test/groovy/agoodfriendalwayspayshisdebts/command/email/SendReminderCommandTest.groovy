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
    def command = new SendReminderCommand(recipientsUuids: recipientsUuids, eventLink: "http://link.com")

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1

    where:
    recipientsUuids << [[], null]
  }
  def "a null or blank event link is a violation"() {
    given:
    def command = new SendReminderCommand(eventLink: eventLink, recipientsUuids: [null])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 1

    where:
    eventLink << [null, ""]
  }

  def "the event link must be a valid URL"() {
    given:
    def command = new SendReminderCommand(eventLink: eventLink, recipientsUuids: [null])

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == errors

    where:
    eventLink                                      || errors
    "link"                                         || 1
    "http://link.com/events/123-ez34-345/expenses" || 0
  }
}
