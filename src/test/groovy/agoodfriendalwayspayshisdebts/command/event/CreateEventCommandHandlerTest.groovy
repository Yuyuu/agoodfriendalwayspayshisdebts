package agoodfriendalwayspayshisdebts.command.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
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
}
