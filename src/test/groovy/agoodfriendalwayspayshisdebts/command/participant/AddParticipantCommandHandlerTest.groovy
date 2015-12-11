package agoodfriendalwayspayshisdebts.command.participant

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class AddParticipantCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  Event event = new Event("", [])

  def setup() {
    RepositoryLocator.events().add(event)
  }

  def "adds the participant to the event"() {
    given:
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1, email: "", expensesUuids: [])

    when:
    new AddParticipantCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(event.id)
    event.participants().find { it.name() == "lea"} != null
  }

  def "includes the participant in the selected expenses"() {
    given:
    def expense = new Expense("", null, 1, [], event.id)
    event.expenses().add(expense)
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1, email: "", expensesUuids: [expense.id.toString()])

    when:
    def participantId = new AddParticipantCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(event.id)
    event.expenses().first().participantsIds().contains(participantId)
  }

  def "sets an empty email for the participant if none is provided"() {
    given:
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1, expensesUuids: [])

    when:
    new AddParticipantCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(event.id)
    event.participants().find({ it.name() == "lea"}).email() == ""
  }

  def "can add a participant without including him in any expense"() {
    given:
    def expense = Mock(Expense)
    event.expenses().add(expense)

    when:
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1)
    new AddParticipantCommandHandler().execute(command)

    then:
    0 * expense.includeParticipant(_ as Participant)
  }

  def "records the operation when a participant is added"() {
    when:
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1)
    new AddParticipantCommandHandler().execute(command)

    then:
    def operation = event.operations().first()
    operation.type() == OperationType.NEW_PARTICIPANT
    operation.data() == "lea"

    and:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.operationId != null
  }
}
