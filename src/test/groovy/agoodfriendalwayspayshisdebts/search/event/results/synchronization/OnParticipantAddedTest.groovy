package agoodfriendalwayspayshisdebts.search.event.results.synchronization

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
  String strKimId = kim.id.toString()
  Participant lea = new Participant("lea", 2, null)
  String strLeaId = lea.id.toString()
  Event event = new Event("", "€", [kim])

  OnParticipantAdded handler

  def setup() {
    handler = new OnParticipantAdded(jongo.jongo())
    RepositoryLocator.events().add(event)
  }

  def "includes the new participant in the event result"() {
    given:
    jongo.collection("eventresults_view") << [_id: event.id, participantsResults: [(strKimId): [debtsDetail: [:]]]]

    when:
    handler.executeInternalEvent(new ParticipantAddedInternalEvent(event.id, lea))

    then:
    def leaDocument = jongo.collection("eventresults_view").findOne()["participantsResults"][strLeaId]
    leaDocument["participantName"] == "lea"
    leaDocument["participantShare"] == 2
    leaDocument["totalSpent"] == 0D
    leaDocument["totalDebt"] == 0D
    leaDocument["totalAdvance"] == 0D
    leaDocument["details"][strKimId]["participantName"] == "kim"
    leaDocument["details"][strKimId]["rawDebt"] == 0D
    leaDocument["details"][strKimId]["mitigatedDebt"] == 0D
    leaDocument["details"][strKimId]["advance"] == 0D

    and:
    def kimDocument = jongo.collection("eventresults_view").findOne()["participantsResults"][strKimId]
    kimDocument["details"][strLeaId]["participantName"] == "lea"
    kimDocument["details"][strLeaId]["rawDebt"] == 0D
    kimDocument["details"][strLeaId]["mitigatedDebt"] == 0D
    kimDocument["details"][strLeaId]["advance"] == 0D
  }
}
