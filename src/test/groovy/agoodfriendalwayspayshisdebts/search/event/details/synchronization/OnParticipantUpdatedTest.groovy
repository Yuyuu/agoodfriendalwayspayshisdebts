package agoodfriendalwayspayshisdebts.search.event.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantUpdatedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnParticipantUpdatedTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  @Rule
  WithJongo jongo = new WithJongo()

  Participant participant = new Participant("", 1, "participant@email.com")
  Event event = new Event("", "€", [participant])

  OnParticipantUpdated handler

  def setup() {
    RepositoryLocator.events().add(event)
    handler = new OnParticipantUpdated(jongo.jongo())
  }

  def "updates the list of participants"() {
    given:
    jongo.collection("eventdetails_view") << [_id: event.id, participants: [[id: participant.id, email: ""]]]

    when:
    handler.executeInternalEvent(new ParticipantUpdatedInternalEvent(event.id, participant.id))

    then:
    def document = jongo.collection("eventdetails_view").findOne()["participants"][0]
    document["email"] == "participant@email.com"
  }
}
