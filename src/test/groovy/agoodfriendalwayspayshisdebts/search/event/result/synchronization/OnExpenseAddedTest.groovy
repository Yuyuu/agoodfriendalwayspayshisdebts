package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnExpenseAddedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  def kim = new Participant("kim", 1, null)
  def ben = new Participant("ben", 1, null)
  def event = new Event("", [kim, ben])

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "can update the result of the event"() {
    given:
    def expense = new Expense("label", ben.id(), 2D, [kim.id(), ben.id()])
    event.expenses().add(expense)
    RepositoryLocator.events().save(event)

    and:
    jongo.collection("eventresult_view") << [
        _id: event.id,
        participantsResults: [
            (kim.id().toString()): [participantName: "kim", totalSpent: 5D, totalDebt: 0D, debtsDetail: [(ben.id().toString()): [creditorName: "ben", amount:0D]]],
            (ben.id().toString()): [participantName: "ben", totalSpent: 0D, totalDebt: 2.5D, debtsDetail: [(kim.id().toString()): [creditorName: "kim", amount:2.5D]]]
        ]
    ]

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    def participantsResultsDocument = jongo.collection("eventresult_view").findOne()["participantsResults"]
    participantsResultsDocument[kim.id().toString()]["totalSpent"] == 5D
    participantsResultsDocument[ben.id().toString()]["totalSpent"] == 2D
    participantsResultsDocument[kim.id().toString()]["totalDebt"] == 0D
    participantsResultsDocument[ben.id().toString()]["totalDebt"] == 1.5D
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][kim.id().toString()] == null
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][ben.id().toString()]["amount"] == 0D
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][ben.id().toString()]["creditorName"] == "ben"
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][kim.id().toString()]["amount"] == 1.5D
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][kim.id().toString()]["creditorName"] == "kim"
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][ben.id().toString()] == null
  }

  def "can create the result if it does not exist yet"() {
    given:
    def expense = new Expense("label", kim.id(), 4D, [kim.id(), ben.id()])
    event.expenses().add(expense)
    RepositoryLocator.events().save(event)

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, expense))

    then:
    def resultDocument = jongo.collection("eventresult_view").findOne()
    resultDocument["_id"] == event.id

    and:
    def participantsResultsDocument = resultDocument["participantsResults"]
    participantsResultsDocument[kim.id().toString()]["totalSpent"] == 4D
    participantsResultsDocument[ben.id().toString()]["totalSpent"] == 0D
    participantsResultsDocument[kim.id().toString()]["totalDebt"] == 0D
    participantsResultsDocument[ben.id().toString()]["totalDebt"] == 2D
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][kim.id().toString()] == null
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][ben.id().toString()]["amount"] == 0D
    participantsResultsDocument[kim.id().toString()]["debtsDetail"][ben.id().toString()]["creditorName"] == "ben"
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][kim.id().toString()]["amount"] == 2D
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][kim.id().toString()]["creditorName"] == "kim"
    participantsResultsDocument[ben.id().toString()]["debtsDetail"][ben.id().toString()] == null
  }
}
