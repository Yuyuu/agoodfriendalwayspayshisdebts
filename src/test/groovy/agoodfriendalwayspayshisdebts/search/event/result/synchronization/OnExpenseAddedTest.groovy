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
  def event = new Event("event", [kim, ben])

  OnExpenseAdded handler

  def setup() {
    handler = new OnExpenseAdded(jongo.jongo())
  }

  def "can update the result of the event"() {
    given:
    event.expenses().add(new Expense("label", kim.id(), 3.56D, [kim.id(), ben.id()]))
    RepositoryLocator.events().save(event)

    and:
    jongo.collection("eventresult_view") << [
        _id: event.id,
        participantsResults: [
            (kim.id().toString()): [totalSpent: 5D, participantDebtsDetails:[totalDebt: 3D, details: [(ben.id().toString()): 3D]]]
        ]
    ]

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, null))

    then:
    def resultDocument = jongo.collection("eventresult_view").findOne()
    resultDocument["participantsResults"][kim.id().toString()]["totalSpent"] == 3.56D
    resultDocument["participantsResults"][ben.id().toString()]["participantDebtsDetails"]["details"][kim.id().toString()] == 1.78D
  }

  def "can create the result if it does not exist yet"() {
    given:
    event.expenses().add(new Expense("label", kim.id(), 3.56D, [kim.id(), ben.id()]))
    RepositoryLocator.events().save(event)

    when:
    handler.executeEvent(new ExpenseAddedInternalEvent(event.id, null))

    then:
    def resultDocument = jongo.collection("eventresult_view").findOne()
    resultDocument["participantsResults"][kim.id().toString()]["totalSpent"] == 3.56D
    resultDocument["participantsResults"][ben.id().toString()]["participantDebtsDetails"]["details"][kim.id().toString()] == 1.78D
  }
}
