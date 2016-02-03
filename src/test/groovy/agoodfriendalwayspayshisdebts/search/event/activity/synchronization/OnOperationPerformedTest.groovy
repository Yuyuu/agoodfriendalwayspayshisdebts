package agoodfriendalwayspayshisdebts.search.event.activity.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnOperationPerformedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  OnOperationPerformed handler

  def setup() {
    handler = new OnOperationPerformed(jongo.jongo())
  }

  def "saves the operation details in the repository"() {
    given:
    def eventId = UUID.randomUUID()
    def internalEvent = new OperationPerformedInternalEvent(eventId, OperationType.REMINDER_DROPPED, "hello")

    when:
    handler.executeInternalEvent(internalEvent)

    then:
    def document = jongo.collection("operationdetails_view").findOne()
    document["_id"] != null
    document["operationType"] == "REMINDER_DROPPED"
    document["creationDate"] == internalEvent.creationDate.millis
    document["operationData"] == "hello"
    document["eventId"] == eventId
  }
}
