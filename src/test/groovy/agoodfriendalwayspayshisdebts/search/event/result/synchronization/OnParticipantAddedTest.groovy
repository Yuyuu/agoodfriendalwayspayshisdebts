package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnParticipantAddedTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  @Rule
  WithJongo jongo = new WithJongo()

  Participant kim = new Participant("kim", 1, null)
  String strKimId = kim.id().toString()
  def lea = new Participant("lea", 2, null)
  String strLeaId = lea.id().toString()
  Event event = new Event("", [kim])

  OnParticipantAdded handler

  def setup() {
    handler = new OnParticipantAdded(jongo.jongo())
    RepositoryLocator.events().save(event)
  }

  def "includes the new participant in the event result"() {
    given:
    jongo.collection("eventresult_view") << [_id: event.id, participantsResults: [(strKimId): [debtsDetail: [:]]]]

    when:
    handler.executeInternalEvent(new ParticipantAddedInternalEvent(event.id, lea))

    then:
    def leaDocument = jongo.collection("eventresult_view").findOne()["participantsResults"][strLeaId]
    leaDocument["participantName"] == "lea"
    leaDocument["participantShare"] == 2
    leaDocument["totalSpent"] == 0D
    leaDocument["totalDebt"] == 0D
    leaDocument["debtsDetail"][strKimId]["creditorName"] == "kim"
    leaDocument["debtsDetail"][strKimId]["rawAmount"] == 0D
    leaDocument["debtsDetail"][strKimId]["mitigatedAmount"] == 0D

    and:
    def kimDocument = jongo.collection("eventresult_view").findOne()["participantsResults"][strKimId]
    kimDocument["debtsDetail"][strLeaId]["creditorName"] == "lea"
    kimDocument["debtsDetail"][strLeaId]["rawAmount"] == 0D
    kimDocument["debtsDetail"][strLeaId]["mitigatedAmount"] == 0D
  }
}
