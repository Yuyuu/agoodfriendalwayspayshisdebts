package agoodfriendalwayspayshisdebts.search.event.results.synchronization

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
    jongo.collection("eventresults_view") << [
        _id: eventId,
        participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 5D, totalDebt: 0D, totalAdvance: 2.5D, details: [(strBenId): [participantName: "ben", rawDebt: 0D, mitigatedDebt: 0D, advance: 2.5D]]],
            (strBenId): [participantName: "ben", participantShare: 1, totalSpent: 0D, totalDebt: 2.5D, totalAdvance: 0D, details: [(strKimId): [participantName: "kim", rawDebt: 2.5D, mitigatedDebt: 2.5D, advance: 0D]]]
        ]
    ]

    when:
    handler.executeInternalEvent(new ExpenseAddedInternalEvent(expense))

    then:
    def participantsResultsDocument = jongo.collection("eventresults_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    kimResultDocument["totalSpent"] == 5D
    kimResultDocument["totalDebt"] == 0D
    kimResultDocument["totalAdvance"] == 1.5D
    kimResultDocument["details"][strKimId] == null
    kimResultDocument["details"][strBenId]["rawDebt"] == 1D
    kimResultDocument["details"][strBenId]["mitigatedDebt"] == 0D
    kimResultDocument["details"][strBenId]["advance"] == 1.5D
    kimResultDocument["details"][strBenId]["participantName"] == "ben"

    and:
    def benResultDocument = participantsResultsDocument[strBenId]
    benResultDocument["totalSpent"] == 2D
    benResultDocument["totalDebt"] == 1.5D
    benResultDocument["totalAdvance"] == 0D
    benResultDocument["details"][strBenId] == null
    benResultDocument["details"][strKimId]["rawDebt"] == 2.5D
    benResultDocument["details"][strKimId]["mitigatedDebt"] == 1.5D
    benResultDocument["details"][strKimId]["advance"] == 0D
    benResultDocument["details"][strKimId]["participantName"] == "kim"
  }
}
