package agoodfriendalwayspayshisdebts.search.event.results.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnParticipantIncludedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Participant kim = new Participant("kim", 1, null)
  String strKimId = kim.id.toString()
  Participant lea = new Participant("lea", 1, null)
  String strLeaId = lea.id.toString()
  Participant ben = new Participant("ben", 2, null)
  String strBenId = ben.id.toString()
  Event event = new Event("", "", [kim, lea, ben])

  OnParticipantIncluded handler

  def setup() {
    handler = new OnParticipantIncluded(jongo.jongo())
    RepositoryLocator.events().add(event)
  }

  def "initializes the results of the participant if it does not exist"() {
    given:
    def expense = new Expense("label", kim.id, 9D, [kim.id], event.id)

    and:
    jongo.collection("eventresults_view") << [
        _id: event.id, participantsResults: [
        (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 9D, totalDebt: 2D, totalAdvance: 4.5D, details: [:]]
    ]
    ]

    when:
    expense.participantsIds() << ben.id
    handler.executeInternalEvent(new ParticipantIncludedInternalEvent(expense, ben))

    then:
    def participantsResultsDocument = jongo.collection("eventresults_view").findOne()["participantsResults"]
    def benResultDocument = participantsResultsDocument[strBenId]
    benResultDocument["totalSpent"] != null
    benResultDocument["totalDebt"] != null
    benResultDocument["totalAdvance"] != null
    benResultDocument["details"][strKimId]["rawDebt"] != null
    benResultDocument["details"][strKimId]["mitigatedDebt"] != null
    benResultDocument["details"][strKimId]["advance"] != null
  }

  def "can update the result of the event"() {
    given:
    def expense = new Expense("label", kim.id, 9D, [kim.id, lea.id], event.id)

    and:
    jongo.collection("eventresults_view") << [
        _id: event.id, participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 9D, totalDebt: 2D, totalAdvance: 4.5D, details: [(strBenId): [participantName: "ben", rawDebt: 2D, mitigatedDebt: 2D, advance: 0D], (strLeaId): [advance: 4.5D]]],
            (strLeaId): [participantName: "lea", participantShare: 1, totalSpent: 0D, totalDebt: 4.5D, totalAdvance: 0D, details: [(strKimId): [participantName: "kim", rawDebt: 4.5D, mitigatedDebt: 4.5D, advance: 0D], (strBenId): [:]]],
            (strBenId): [participantName: "ben", participantShare: 2, totalSpent: 0D, totalDebt: 0D, totalAdvance: 2D, details: [(strKimId): [participantName: "kim", rawDebt: 0D, mitigatedDebt: 0D, advance: 2D], (strLeaId): [:]]]
        ]
    ]

    when:
    expense.participantsIds() << ben.id
    handler.executeInternalEvent(new ParticipantIncludedInternalEvent(expense, ben))

    then:
    def participantsResultsDocument = jongo.collection("eventresults_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    kimResultDocument["totalSpent"] == 9D
    kimResultDocument["totalDebt"] == 0D
    kimResultDocument["totalAdvance"] == 4.75D
    kimResultDocument["details"][strBenId]["rawDebt"] == 2D
    kimResultDocument["details"][strBenId]["mitigatedDebt"] == 0D
    kimResultDocument["details"][strBenId]["advance"] == 2.5D
    kimResultDocument["details"][strLeaId]["rawDebt"] == 0D
    kimResultDocument["details"][strLeaId]["mitigatedDebt"] == 0D
    kimResultDocument["details"][strLeaId]["advance"] == 2.25D

    and:
    def benResultDocument = participantsResultsDocument[strBenId]
    benResultDocument["totalSpent"] == 0D
    benResultDocument["totalDebt"] == 2.5D
    benResultDocument["totalAdvance"] == 0D
    benResultDocument["details"][strKimId]["rawDebt"] == 4.5D
    benResultDocument["details"][strKimId]["mitigatedDebt"] == 2.5D
    benResultDocument["details"][strKimId]["advance"] == 0D
    benResultDocument["details"][strLeaId]["rawDebt"] == 0D
    benResultDocument["details"][strLeaId]["mitigatedDebt"] == 0D
    benResultDocument["details"][strLeaId]["advance"] == 0D

    and:
    def leaResultDocument = participantsResultsDocument[strLeaId]
    leaResultDocument["totalSpent"] == 0D
    leaResultDocument["totalDebt"] == 2.25D
    leaResultDocument["totalAdvance"] == 0D
    leaResultDocument["details"][strKimId]["rawDebt"] == 2.25D
    leaResultDocument["details"][strKimId]["mitigatedDebt"] == 2.25D
    leaResultDocument["details"][strKimId]["advance"] == 0D
    leaResultDocument["details"][strBenId]["rawDebt"] == 0D
    leaResultDocument["details"][strBenId]["mitigatedDebt"] == 0D
    leaResultDocument["details"][strBenId]["advance"] == 0D
  }
}
