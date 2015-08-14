package agoodfriendalwayspayshisdebts.model.event

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class EventTest extends Specification {

  @Rule
  WithEventBus eventBus = new WithEventBus()

  def "can create an event with a name and a list of participants"() {
    given:
    def event = new Event("cool event", [new Participant("kim", 1, null)])

    expect:
    event.id != null
    event.name() == "cool event"
    event.participants().first().name() == "kim"
  }

  def "can emit an internal event when creating a new event"() {
    when:
    def event = Event.createAndPublishEvent("cool event", [new Participant("kim", 1, null)])

    then:
    def internalEvent = eventBus.bus.lastEvent(EventCreatedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
  }

  def "contains expenses"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("cool event", [kim])

    when:
    def expense = new Expense("label", kim.id(), 5, [kim.id()])
    event.addExpense(expense)

    then:
    event.expenses().size() == 1
    event.expenses()[0] == expense
  }
}
