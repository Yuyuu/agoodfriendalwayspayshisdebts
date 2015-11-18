package agoodfriendalwayspayshisdebts.command.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class CreateEventCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  def "can add the event to the repository"() {
    given:
    def command = new CreateEventCommand(name: "cool event", participants: [[name: "kim", share: 1]])

    when:
    UUID id = new CreateEventCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(id)
    event.name() == "cool event"
    event.participants().first().name() == "kim"
  }

  def "records the operation when an event is created"() {
    when:
    UUID id = new CreateEventCommandHandler().execute(new CreateEventCommand(name: "hello", participants: []))

    then:
    def event = RepositoryLocator.events().get(id)
    def operation = event.operations().first()
    operation.type() == OperationType.EVENT_CREATION
    operation.data() == "hello"

    and:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.operationId != null
  }
}
