package agoodfriendalwayspayshisdebts.command.reminder

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.MemoryOperationRepository
import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
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

  def "records the state of the reminder as an operation"() {
    when:
    handler.execute(new RecordReminderStateCommand(event: state, eventId: event.id, participantId: bob.id))

    then:
    def operation = ((MemoryOperationRepository) RepositoryLocator.operations()).all[0]
    operation.id != null
    operation.type() == operationType
    operation.data() == "bob"
    operation.creationDate()!= null

    where:
    state       || operationType
    "dropped"   || OperationType.REMINDER_DROPPED
    "delivered" || OperationType.REMINDER_DELIVERED
  }
}
