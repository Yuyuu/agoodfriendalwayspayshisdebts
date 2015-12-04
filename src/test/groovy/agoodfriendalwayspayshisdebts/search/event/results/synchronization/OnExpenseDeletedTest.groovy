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
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 6D, totalDebt: 0D, totalAdvance: 3D, details: [(strBenId): [participantName: "ben", rawDebt: 1D, mitigatedDebt: 0D, advance: 2D]]],
            (strBenId): [participantName: "ben", participantShare: 1, totalSpent: 2D, totalDebt: 2D, totalAdvance: 0D, details: [(strKimId): [participantName: "kim", rawDebt: 3D, mitigatedDebt: 2D, advance: 0D]]]
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
    kimResultDocument["totalAdvance"] == 2D
    benResultDocument["totalAdvance"] == 0D
    kimResultDocument["details"][strBenId]["rawDebt"] == 1D
    kimResultDocument["details"][strBenId]["mitigatedDebt"] == 0D
    kimResultDocument["details"][strBenId]["advance"] == 1D
    benResultDocument["details"][strKimId]["rawDebt"] == 2D
    benResultDocument["details"][strKimId]["mitigatedDebt"] == 1D
    benResultDocument["details"][strKimId]["advance"] == 0D
  }
}
