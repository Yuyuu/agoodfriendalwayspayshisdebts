package agoodfriendalwayspayshisdebts.command.expense

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class AddExpenseCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  Participant kim = new Participant("kim", 1, null)
  Event event = new Event("event", [kim])

  def setup() {
    RepositoryLocator.events().add(event)
  }

  def "can add the expense to an event"() {
    when:
    new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(
            eventId: event.id, label: "label", purchaserUuid: kim.id.toString(), amount: 1,
            participantsUuids: [kim.id.toString()], description: "description"
        )
    )

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.label() == "label"
    expense.purchaserId() == kim.id
    expense.amount() == 1
    expense.participantsIds() == [kim.id] as Set
    expense.description() == "description"
  }

  def "returns the details of the added expense"() {
    when:
    def details = new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(
            eventId: event.id, label: "label", purchaserUuid: kim.id.toString(), amount: 1,
            participantsUuids: [kim.id.toString()], description: "description"
        )
    )

    then:
    details.label == "label"
    details.purchaserName == "kim"
    details.amount == 1
    details.participantsNames == ["kim"]
    details.description == "description"
  }

  def "shares the expense between all the participants of the event if none is specified"() {
    given:
    def lea = new Participant("lea", 1, null)
    event.participants().add(lea)
    RepositoryLocator.events().add(event)

    when:
    new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(eventId: event.id, label: "test", purchaserUuid: kim.id.toString(), amount: 1)
    )

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.participantsIds() == event.participants()*.id as Set
  }

  def "adds an empty description if none is provided"() {
    when:
    new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(eventId: event.id, label: "test", purchaserUuid: kim.id.toString(), amount: 1)
    )

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.description() == ""
  }

  def "trims the description of the event"() {
    when:
    new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(
            eventId: event.id, label: "test", purchaserUuid: kim.id.toString(), amount: 1,
            description: "     hello  hi       "
        )
    )

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.description() == "hello  hi"
  }

  def "records the operation when an expense is added"() {
    when:
    new AddExpenseCommandHandler().execute(
        new AddExpenseCommand(eventId: event.id, label: "label", purchaserUuid: kim.id.toString(), amount: 1)
    )

    then:
    def operation = event.operations().first()
    operation.type() == OperationType.NEW_EXPENSE
    operation.data() == "label"

    and:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.operationId != null
  }
}
