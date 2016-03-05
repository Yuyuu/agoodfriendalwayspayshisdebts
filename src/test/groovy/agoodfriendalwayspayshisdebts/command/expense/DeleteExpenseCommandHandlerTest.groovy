package agoodfriendalwayspayshisdebts.command.expense

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
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

  Event event = new Event("", "€", [])
  Expense expense = new Expense("label", null, 1, [], event.id)

  def setup() {
    event.expenses().add(expense)
    RepositoryLocator.events().add(event);
  }

  def "deletes the expense from the event"() {
    when:
    new DeleteExpenseCommandHandler().execute(new DeleteExpenseCommand(eventId: event.id, expenseId: expense.id))

    then:
    def updatedEvent = RepositoryLocator.events().get(event.id)
    !updatedEvent.expenses().contains(expense)
  }
}
