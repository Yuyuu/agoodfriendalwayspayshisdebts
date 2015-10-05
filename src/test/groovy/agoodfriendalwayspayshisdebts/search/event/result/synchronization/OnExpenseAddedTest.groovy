package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  Participant kim = new Participant("kim", 1, null)
  String strKimId = kim.id().toString()
  Participant ben = new Participant("ben", 1, null)
  String strBenId = ben.id().toString()
  UUID eventId = UUID.randomUUID()

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "can update the result of the event"() {
    given:
    def expense = new Expense("label", ben.id(), 2D, [kim.id(), ben.id()], eventId)

    and:
    jongo.collection("eventresult_view") << [
        _id: eventId,
        participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 5D, totalDebt: 0D, debtsDetail: [(strBenId): [creditorName: "ben", rawAmount: 0D, mitigatedAmount: 0D]]],
            (strBenId): [participantName: "ben", participantShare: 1, totalSpent: 0D, totalDebt: 2.5D, debtsDetail: [(strKimId): [creditorName: "kim", rawAmount: 2.5D, mitigatedAmount: 2.5D]]]
        ]
    ]

    when:
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(expense))

    then:
    def participantsResultsDocument = jongo.collection("eventresult_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    kimResultDocument["totalSpent"] == 5D
    kimResultDocument["totalDebt"] == 0D
    kimResultDocument["debtsDetail"][strKimId] == null
    kimResultDocument["debtsDetail"][strBenId]["rawAmount"] == 1D
    kimResultDocument["debtsDetail"][strBenId]["mitigatedAmount"] == 0D
    kimResultDocument["debtsDetail"][strBenId]["creditorName"] == "ben"

    and:
    def benResultDocument = participantsResultsDocument[strBenId]
    benResultDocument["totalSpent"] == 2D
    benResultDocument["totalDebt"] == 1.5D
    benResultDocument["debtsDetail"][strBenId] == null
    benResultDocument["debtsDetail"][strKimId]["rawAmount"] == 2.5D
    benResultDocument["debtsDetail"][strKimId]["mitigatedAmount"] == 1.5D
    benResultDocument["debtsDetail"][strKimId]["creditorName"] == "kim"
  }
}
