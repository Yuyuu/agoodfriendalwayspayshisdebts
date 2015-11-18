package agoodfriendalwayspayshisdebts.command.expense

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class DeleteExpenseCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  Event event = new Event("", [])
  Expense expense = new Expense("label", null, 1, [], event.id)

  def setup() {
    event.expenses().add(expense)
    RepositoryLocator.events().save(event);
  }

  def "deletes the expense from the event"() {
    when:
    new DeleteExpenseCommandHandler().execute(new DeleteExpenseCommand(eventId: event.id, expenseId: expense.id()))

    then:
    def updatedEvent = RepositoryLocator.events().get(event.id)
    !updatedEvent.expenses().contains(expense)
  }

  def "records the operation when an expense is deleted"() {
    when:
    new DeleteExpenseCommandHandler().execute(new DeleteExpenseCommand(eventId: event.id, expenseId: expense.id()))

    then:
    def operation = event.operations().first()
    operation.type() == OperationType.EXPENSE_DELETED
    operation.data() == "label"

    and:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.operationId != null
  }
}
