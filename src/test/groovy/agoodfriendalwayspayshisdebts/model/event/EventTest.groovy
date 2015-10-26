package agoodfriendalwayspayshisdebts.model.event

import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.UnknownParticipant
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
    event.participants().first().eventId() == event.id
  }

  def "emits an internal event when creating a new event"() {
    when:
    def event = Event.createAndPublishInternalEvent("cool event", [new Participant("kim", 1, null)])

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
    def expense = new Expense("label", kim.id(), 5, [kim.id()], event.id)
    event.addExpense(expense)

    then:
    event.expenses().size() == 1
    event.expenses()[0] == expense
  }

  def "emits an event when an expense is added"() {
    given:
    def event = new Event("event", [new Participant("kim", 1, null)])

    when:
    def expense = new Expense("label", null, 2, [], event.id)
    event.addExpense(expense)

    then:
    def internalEvent = eventBus.bus.lastEvent(ExpenseAddedInternalEvent)
    internalEvent != null
    internalEvent.expense == expense
  }

  def "throws an error when attempting to delete an expense that does not exist"() {
    given:
    def event = new Event("", [])

    when:
    event.deleteExpense(UUID.randomUUID())

    then:
    thrown(UnknownExpense)
  }

  def "can delete an expense"() {
    given:
    def event = new Event("", [])
    def expense = new Expense("", null, 1, [], event.id)
    event.expenses().add(expense)

    when:
    event.deleteExpense(expense.id())

    then:
    event.expenses().empty
  }

  def "emits an event when an expense is deleted"() {
    given:
    def event = new Event("", [])
    def expense = new Expense("label", null, 2, [], event.id)
    event.expenses().add(expense)

    when:
    event.deleteExpense(expense.id())

    then:
    def internalEvent = eventBus.bus.lastEvent(ExpenseDeletedInternalEvent)
    internalEvent != null
    internalEvent.expense == expense
  }

  def "contains participant"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("", [kim])

    when:
    def ben = new Participant("ben", 1, null)
    event.addParticipant(ben)

    then:
    ben.eventId() == event.id
    event.participants().size() == 2
    event.participants().find { it.name() == "ben" } != null
  }

  def "emits an event when a participant is added"() {
    given:
    def event = new Event("", [new Participant("kim", 1, null)])

    when:
    def ben = new Participant("ben", 1, null)
    event.addParticipant(ben)

    then:
    def internalEvent = eventBus.bus.lastEvent(ParticipantAddedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.participant == ben
  }

  def "can find a participant"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("", [kim])

    expect:
    event.findParticipant(kim.id()) == kim
  }

  def "throws an error if the searched participant does not exist"() {
    given:
    def event = new Event("", [])

    when:
    event.findParticipant(UUID.randomUUID())

    then:
    thrown(UnknownParticipant)
  }
}
