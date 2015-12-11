package agoodfriendalwayspayshisdebts.search.event.activity.model

import agoodfriendalwayspayshisdebts.model.activity.Operation
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import spock.lang.Specification

class EventOperationTest extends Specification {

  def "can be created for an operation"() {
    given:
    def eventId = UUID.randomUUID()
    def operation = new Operation(OperationType.EVENT_CREATION, "hello", eventId)

    when:
    def eventOperation = EventOperation.forOperation(operation)

    then:
    eventOperation.id == operation.id
    eventOperation.type == operation.type().toString()
    eventOperation.creationDate == operation.creationDate()
    eventOperation.data == "hello"
    eventOperation.eventId == operation.eventId()
  }
}
