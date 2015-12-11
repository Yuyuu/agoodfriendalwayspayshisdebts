package agoodfriendalwayspayshisdebts.search.event.activity.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.Operation
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.event.Event
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnOperationPerformedTest extends Specification {
  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  @Rule
  WithJongo jongo = new WithJongo()

  Event event = new Event("", [])
  Operation operation = new Operation(OperationType.EVENT_CREATION, "hello", event.id)

  OnOperationPerformed handler

  def setup() {
    handler = new OnOperationPerformed(jongo.jongo())
    event.operations() << operation
    RepositoryLocator.events().add(event)
  }

  def "includes the operation in the event activity"() {
    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, operation.id))

    then:
    def document = jongo.collection("eventactivity_view").findOne()
    document["_id"] == operation.id
    document["type"] == "EVENT_CREATION"
    document["creationDate"] == operation.creationDate().millis
    document["data"] == "hello"
    document["eventId"] == event.id
  }

  def "throws an exception if the operation cannot be found"() {
    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, UUID.randomUUID()))

    then:
    thrown(IllegalStateException)
  }
}
