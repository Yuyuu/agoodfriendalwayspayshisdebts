package agoodfriendalwayspayshisdebts.command.participant

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class UpdateParticipantCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  Participant lea = new Participant("lea", 1, "")
  Event event = new Event("", [lea])

  def setup() {
    RepositoryLocator.events().save(event)
  }

  def "updates the participant"() {
    given:
    def command = new UpdateParticipantCommand(eventId: event.id, id: lea.id(), email: "lea@email.com")

    when:
    new UpdateParticipantCommandHandler().execute(command)

    then:
    def lea = RepositoryLocator.events().get(event.id).participants().find {it.name() == "lea"}
    lea.email() == "lea@email.com"
  }
}
