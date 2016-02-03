package agoodfriendalwayspayshisdebts.command.reminder

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class RecordReminderStateCommandHandlerTest extends Specification {
  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  @Rule
  WithEventBus eventBus = new WithEventBus()

  Participant bob = new Participant("bob", 1, null)
  Event event = new Event("", [bob])

  RecordReminderStateCommandHandler handler = new RecordReminderStateCommandHandler()

  def setup() {
    RepositoryLocator.events().add(event)
  }

  def "an event is emitted when the state of a reminder changes"() {
    when:
    handler.execute(new RecordReminderStateCommand(event: state, eventId: event.id, participantId: bob.id))

    then:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.creationDate != null
    internalEvent.operationType == operationType
    internalEvent.operationData == "bob"

    where:
    state       || operationType
    "dropped"   || OperationType.REMINDER_DROPPED
    "delivered" || OperationType.REMINDER_DELIVERED
  }
}
