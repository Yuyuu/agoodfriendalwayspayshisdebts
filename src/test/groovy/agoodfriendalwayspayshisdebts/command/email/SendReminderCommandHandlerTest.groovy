package agoodfriendalwayspayshisdebts.command.email

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import org.junit.Rule
import spock.lang.Specification

class SendReminderCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Participant ben = new Participant("ben", 1, "ben@email.com")
  Event event = new Event("", [ben])

  void setup() {
    RepositoryLocator.events().save(event)
  }

  def "generates fake reports"() {
    given:
    def strBenId = ben.id().toString()

    when:
    def command = new SendReminderCommand(eventId: event.id, recipientsUuids: [strBenId])
    def reports = new SendReminderCommandHandler().execute(command)

    then:
    def report = reports.first()
    report.recipientName == "ben"
    report.success
  }
}
