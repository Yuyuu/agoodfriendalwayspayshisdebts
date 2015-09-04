package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseDeletedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Participant kim = new Participant("kim", 1, null)
  Participant ben = new Participant("ben", 1, null)
  Event event = new Event("", [kim, ben])

  OnExpenseDeleted handler

  def setup() {
    handler = new OnExpenseDeleted(jongo.jongo())
    RepositoryLocator.events().save(event)
  }

  def "can recalculate the result when an expense is deleted"() {
    given:
    def strKimId = kim.id().toString()
    def strBenId = ben.id().toString()
    jongo.collection("eventresult_view") << [
        _id: event.id,
        participantsResults: [
            (strKimId): [participantName: "kim", totalSpent: 6D, totalDebt: 0D, debtsDetail: [(strBenId): [creditorName: "ben", rawAmount: 1D, mitigatedAmount:0D]]],
            (strBenId): [participantName: "ben", totalSpent: 2D, totalDebt: 2D, debtsDetail: [(strKimId): [creditorName: "kim", rawAmount: 3D, mitigatedAmount:2D]]]
        ]
    ]

    when:
    def expense = new Expense("", kim.id(), 2D, [kim.id(), ben.id()])
    handler.executeEvent(new ExpenseDeletedInternalEvent(event.id, expense))

    then:
    def participantsResultsDocument = jongo.collection("eventresult_view").findOne()["participantsResults"]
    def kimResultDocument = participantsResultsDocument[strKimId]
    def benResultDocument = participantsResultsDocument[strBenId]
    kimResultDocument["totalSpent"] == 4D
    benResultDocument["totalSpent"] == 2D
    kimResultDocument["totalDebt"] == 0D
    benResultDocument["totalDebt"] == 1D
    kimResultDocument["debtsDetail"][strBenId]["rawAmount"] == 1D
    kimResultDocument["debtsDetail"][strBenId]["mitigatedAmount"] == 0D
    benResultDocument["debtsDetail"][strKimId]["rawAmount"] == 2D
    benResultDocument["debtsDetail"][strKimId]["mitigatedAmount"] == 1D
  }
}
