package agoodfriendalwayspayshisdebts.search.event.results.synchronization

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseDeletedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  Participant kim = new Participant("kim", 1, null)
  Participant ben = new Participant("ben", 1, null)
  UUID eventId = UUID.randomUUID()

  OnExpenseDeleted handler

  def setup() {
    handler = new OnExpenseDeleted(jongo.jongo())
  }

  def "can recalculate the result when an expense is deleted"() {
    given:
    def strKimId = kim.id().toString()
    def strBenId = ben.id().toString()
    jongo.collection("eventresults_view") << [
        _id: eventId,
        participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 6D, totalDebt: 0D, debtsDetails: [(strBenId): [creditorName: "ben", rawAmount: 1D, mitigatedAmount:0D]]],
            (strBenId): [participantName: "ben", participantShare: 1, totalSpent: 2D, totalDebt: 2D, debtsDetails: [(strKimId): [creditorName: "kim", rawAmount: 3D, mitigatedAmount:2D]]]
        ]
    ]

    when:
    def expense = new Expense("", kim.id(), 2D, [kim.id(), ben.id()], eventId)
    handler.executeInternalEvent(new ExpenseDeletedInternalEvent(expense))

    then:
    def participantsResultsDocument = jongo.collection("eventresults_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    def benResultDocument = participantsResultsDocument[strBenId]
    kimResultDocument["totalSpent"] == 4D
    benResultDocument["totalSpent"] == 2D
    kimResultDocument["totalDebt"] == 0D
    benResultDocument["totalDebt"] == 1D
    kimResultDocument["debtsDetails"][strBenId]["rawAmount"] == 1D
    kimResultDocument["debtsDetails"][strBenId]["mitigatedAmount"] == 0D
    benResultDocument["debtsDetails"][strKimId]["rawAmount"] == 2D
    benResultDocument["debtsDetails"][strKimId]["mitigatedAmount"] == 1D
  }
}
