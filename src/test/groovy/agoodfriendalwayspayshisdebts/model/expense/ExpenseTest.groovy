package agoodfriendalwayspayshisdebts.model.expense

import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class ExpenseTest extends Specification {
  @Rule
  WithEventBus eventBus = new WithEventBus()

  def "can create an expense"() {
    given:
    def purchaserId = UUID.randomUUID()
    def eventId = UUID.randomUUID()
    def expense = new Expense("food", purchaserId, 10, [purchaserId], eventId)

    expect:
    expense.id != null
    expense.label() == "food"
    expense.state() == State.PENDING
    expense.purchaserId() == purchaserId
    expense.amount() == 10
    expense.participantsIds().first() == purchaserId
    expense.eventId() == eventId
  }

  def "is shared between participant"() {
    given:
    def expense = new Expense()

    when:
    def participant = new Participant("lea", 1, "")
    expense.includeParticipant(participant)

    then:
    expense.participantsIds().contains(participant.id)
  }

  def "is shared between at most each participant once"() {
    given:
    def purchaserId = UUID.randomUUID()
    def expense = new Expense(participantsIds: [purchaserId])

    when:
    expense.participantsIds().add(purchaserId)

    then:
    expense.participantsIds().size() == 1
  }

  def "has an optional description"() {
    given:
    def expense = new Expense()

    when:
    expense.description = "a description"

    then:
    expense.description() == "a description"
  }

  def "two expenses with the same id are equal"() {
    given:
    def expense1 = new Expense()
    def expense2 = new Expense(id: expense1.id)

    expect:
    expense1 == expense2
  }

  def "emits an event when a participant is included"() {
    given:
    def expense = new Expense()

    when:
    def participant = new Participant("lea", 1, "")
    expense.includeParticipant(participant)

    then:
    def internalEvent = eventBus.bus.lastEvent(ParticipantIncludedInternalEvent)
    internalEvent.expense == expense
    internalEvent.participant == participant
  }
}
