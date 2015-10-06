package agoodfriendalwayspayshisdebts.command.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
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
    RepositoryLocator.events().save(event)
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
    def command = new AddParticipantCommand(eventId: event.id, name: "lea", share: 1, email: "", expensesUuids: [expense.id().toString()])

    when:
    def participantId = new AddParticipantCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(event.id)
    event.expenses().first().participantsIds().contains(participantId)
  }
}
