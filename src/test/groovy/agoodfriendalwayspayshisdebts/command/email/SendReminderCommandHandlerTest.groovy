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

  Participant lea = new Participant("lea", 1, "lea@email.com")
  Participant ben = new Participant("ben", 1, "ben@email.com")
  String strLeaId = lea.id().toString()
  String strBenId = ben.id().toString()
  Event event = new Event("", [lea, ben])

  void setup() {
    RepositoryLocator.events().save(event)
  }

  def "generates fake reports"() {
    when:
    def command = new SendReminderCommand(eventId: event.id, recipientsUuids: [strLeaId, strBenId])
    def reports = new SendReminderCommandHandler().execute(command)

    then:
    reports.size() == 2
    def report = reports.first()
    report.recipientName == "lea"
    report.success
  }

  def "only send reminders to the given recipients"() {
    when:
    def command = new SendReminderCommand(eventId: event.id, recipientsUuids: [strBenId])
    def reports = new SendReminderCommandHandler().execute(command)

    then:
    reports.size() == 1
    reports[0].recipientName == "ben"
  }
}
