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
    RepositoryLocator.events().save(event)
  }

  def "includes the operation in the event activity"() {
    given:
    jongo.collection("eventactivity_view") << [_id: event.id, operations: []]

    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, operation.id()))

    then:
    def document = jongo.collection("eventactivity_view").findOne()
    document["_id"] == event.id
    document["operations"][0]["id"] == operation.id().toString()
    document["operations"][0]["type"] == "EVENT_CREATION"
    document["operations"][0]["creationDate"] == operation.creationDate().toString()
    document["operations"][0]["data"] == "hello"
    document["operations"][0]["eventId"] == event.id.toString()
  }

  def "stores the operations in reverse order"() {
    given:
    jongo.collection("eventactivity_view") << [_id: event.id, operations: [[id: UUID.randomUUID().toString()]]]

    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, operation.id()))

    then:
    def document = jongo.collection("eventactivity_view").findOne()
    document["operations"].size() == 2
    document["operations"][0]["id"] == operation.id().toString()
  }

  def "creates the document if it does not exist yet"() {
    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, operation.id()))

    then:
    def document = jongo.collection("eventactivity_view").findOne()
    document["_id"] == event.id
    document["operations"].size() == 1
  }

  def "throws an exception if the operation cannot be found"() {
    when:
    handler.executeInternalEvent(new OperationPerformedInternalEvent(event.id, UUID.randomUUID()))

    then:
    thrown(IllegalStateException)
  }
}
