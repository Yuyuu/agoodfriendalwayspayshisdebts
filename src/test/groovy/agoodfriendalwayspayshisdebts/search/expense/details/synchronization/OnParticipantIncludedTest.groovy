package agoodfriendalwayspayshisdebts.search.expense.details.synchronization

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

  Participant kim = new Participant("kim", 1, null)
  Event event = new Event("event", [kim])

  OnParticipantIncluded handler

  def setup() {
    handler = new OnParticipantIncluded(jongo.jongo())
  }

  def "updates the participants names of the expense"() {
    given:
    def expense = new Expense("", kim.id, 2, [kim.id], event.id)
    jongo.collection("expensesdetails_view") << [
        _id: event.id,
        expenseCount: 1,
        expenses: [[id: expense.id, label: "label", purchaserName: kim.name(), amount: 2, participantsNames: [kim.name()]]]
    ]

    when:
    def bob = new Participant("bob", 1, "")
    handler.executeInternalEvent(new ParticipantIncludedInternalEvent(expense, bob))

    then:
    def expenseDocument = jongo.collection("expensesdetails_view").findOne()["expenses"][0]
    expenseDocument["participantsNames"][0] == "kim"
    expenseDocument["participantsNames"][1] == "bob"
  }
}
