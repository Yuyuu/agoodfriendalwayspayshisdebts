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

  def "deletes the expense from the event"() {
    given:
    def event = new Event("", [])
    def expense = new Expense("", null, 1, [])
    event.expenses().add(expense)
    RepositoryLocator.events().save(event);

    and:
    def command = new DeleteExpenseCommand(eventId: event.id, expenseId: expense.id())

    when:
    new DeleteExpenseCommandHandler().execute(command)

    then:
    def updatedEvent = RepositoryLocator.events().get(event.id)
    !updatedEvent.expenses().contains(expense)
  }
}
