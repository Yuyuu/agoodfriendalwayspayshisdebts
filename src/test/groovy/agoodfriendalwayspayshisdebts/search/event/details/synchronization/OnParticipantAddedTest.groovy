package agoodfriendalwayspayshisdebts.search.event.details.synchronization

import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnParticipantAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  OnParticipantAdded handler

  def setup() {
    handler = new OnParticipantAdded(jongo.jongo())
  }

  def "updates the list of participants"() {
    given:
    def eventId = UUID.randomUUID()
    jongo.collection("eventdetails_view") << [_id: eventId, participants: []]

    when:
    def lea = new Participant("lea", 2, "lea@email.com")
    handler.executeInternalEvent(new ParticipantAddedInternalEvent(eventId, lea))

    then:
    def leaDocument = jongo.collection("eventdetails_view").findOne()["participants"][0]
    leaDocument["id"] == lea.id
    leaDocument["name"] == "lea"
    leaDocument["share"] == 2
    leaDocument["email"] == "lea@email.com"
  }
}
