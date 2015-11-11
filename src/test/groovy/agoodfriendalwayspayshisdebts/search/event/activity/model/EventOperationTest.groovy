package agoodfriendalwayspayshisdebts.search.event.activity.model

import agoodfriendalwayspayshisdebts.model.activity.Operation
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import spock.lang.Specification

class EventOperationTest extends Specification {

  def "can be created for an operation"() {
    given:
    def eventId = UUID.randomUUID()
    def operation = new Operation(OperationType.EVENT_CREATION, eventId)

    when:
    def eventOperation = EventOperation.forOperation(operation)

    then:
    eventOperation.id == operation.id().toString()
    eventOperation.type == operation.type().toString()
    eventOperation.creationDate == operation.creationDate().toString()
    eventOperation.eventId == operation.eventId().toString()
  }
}
