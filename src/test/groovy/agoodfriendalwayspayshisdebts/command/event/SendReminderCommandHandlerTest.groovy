package agoodfriendalwayspayshisdebts.command.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.infrastructure.services.SMTPEmailSender
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class SendReminderCommandHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  SMTPEmailSender emailSender = Mock(SMTPEmailSender)

  Participant lea = new Participant("lea", 1, "lea@email.com")
  Participant ben = new Participant("ben", 1, "ben@email.com")
  String strLeaId = lea.id().toString()
  String strBenId = ben.id().toString()
  Event event = new Event("", [lea, ben])

  SendReminderCommandHandler handler

  void setup() {
    RepositoryLocator.events().save(event)
    jongo.collection("eventresults_view") << [_id: event.id, participantsResults: [(strLeaId): [:], (strBenId): [:]]]
    handler = new SendReminderCommandHandler(jongo.jongo(), emailSender)
  }

  def "sends a reminder with the provided locale to each recipient and generates a report"() {
    when:
    def command = new SendReminderCommand(
        eventId: event.id, recipientsUuids: [strLeaId, strBenId],
        locale: "fr", eventLink: "http://link"
    )
    def reports = handler.execute(command)

    then:
    2 * emailSender.send({it.locale == Locale.FRANCE})
    reports.size() == 2
  }

  def "still generates a report if an exception occurs when sending the reminder"() {
    given:
    emailSender.send({it.to() == "lea@email.com"}) >> { throw new RuntimeException() }

    when:
    def command = new SendReminderCommand(
        eventId: event.id, recipientsUuids: [strLeaId, strBenId],
        locale: "fr", eventLink: "http://link"
    )
    def reports = handler.execute(command)

    then:
    reports.size() == 2
    reports.find {!it.success} != null
  }

  def "uses the default locale for the reminder if none is provided"() {
    when:
    def command = new SendReminderCommand(
        eventId: event.id, recipientsUuids: [strLeaId], eventLink: "http://link"
    )
    handler.execute(command)

    then:
    1 * emailSender.send({it.locale == Locale.UK})
  }

  def "uses the default locale for the reminder if the provided one is not handled"() {
    when:
    def command = new SendReminderCommand(
        eventId: event.id, recipientsUuids: [strLeaId],
        locale: "de", eventLink: "http://link"
    )
    handler.execute(command)

    then:
    1 * emailSender.send({it.locale == Locale.UK})
  }
}
