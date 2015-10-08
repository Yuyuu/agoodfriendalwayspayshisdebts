package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnParticipantIncludedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  Participant kim = new Participant("kim", 1, null)
  String strKimId = kim.id().toString()
  Participant lea = new Participant("lea", 1, null)
  String strLeaId = lea.id().toString()
  Participant ben = new Participant("ben", 2, null)
  String strBenId = ben.id().toString()
  UUID eventId = UUID.randomUUID()

  OnParticipantIncluded handler

  def setup() {
    handler = new OnParticipantIncluded(jongo.jongo())
  }

  def "can update the result of the event"() {
    given:
    def expense = new Expense("label", kim.id(), 9D, [kim.id(), lea.id()], eventId)

    and:
    jongo.collection("eventresult_view") << [
        _id: eventId, participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 9D, totalDebt: 2D, debtsDetail: [(strBenId): [creditorName: "ben", rawAmount: 2D, mitigatedAmount: 2D], (strLeaId): [:]]],
            (strLeaId): [participantName: "lea", participantShare: 1, totalSpent: 0D, totalDebt: 4.5D, debtsDetail: [(strKimId): [creditorName: "kim", rawAmount: 4.5D, mitigatedAmount: 4.5D], (strBenId): [:]]],
            (strBenId): [participantName: "ben", participantShare: 2, totalSpent: 0D, totalDebt: 0D, debtsDetail: [(strKimId): [creditorName: "kim", rawAmount: 0D, mitigatedAmount: 0D], (strLeaId): [:]]]
        ]
    ]

    when:
    expense.participantsIds() << ben.id()
    handler.executeInternalEvent(new ParticipantIncludedInternalEvent(expense, ben))

    then:
    def participantsResultsDocument = jongo.collection("eventresult_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    kimResultDocument["totalSpent"] == 9D
    kimResultDocument["totalDebt"] == 0D
    kimResultDocument["debtsDetail"][strBenId]["rawAmount"] == 2D
    kimResultDocument["debtsDetail"][strBenId]["mitigatedAmount"] == 0D
    kimResultDocument["debtsDetail"][strLeaId]["rawAmount"] == 0D
    kimResultDocument["debtsDetail"][strLeaId]["mitigatedAmount"] == 0D

    and:
    def benResultDocument = participantsResultsDocument[strBenId]
    benResultDocument["totalSpent"] == 0D
    benResultDocument["totalDebt"] == 2.5D
    benResultDocument["debtsDetail"][strKimId]["rawAmount"] == 4.5D
    benResultDocument["debtsDetail"][strKimId]["mitigatedAmount"] == 2.5D
    benResultDocument["debtsDetail"][strLeaId]["rawAmount"] == 0D
    benResultDocument["debtsDetail"][strLeaId]["mitigatedAmount"] == 0D

    and:
    def leaResultDocument = participantsResultsDocument[strLeaId]
    leaResultDocument["totalSpent"] == 0D
    leaResultDocument["totalDebt"] == 2.25D
    leaResultDocument["debtsDetail"][strKimId]["rawAmount"] == 2.25D
    leaResultDocument["debtsDetail"][strKimId]["mitigatedAmount"] == 2.25D
    leaResultDocument["debtsDetail"][strBenId]["rawAmount"] == 0D
    leaResultDocument["debtsDetail"][strBenId]["mitigatedAmount"] == 0D
  }
}
