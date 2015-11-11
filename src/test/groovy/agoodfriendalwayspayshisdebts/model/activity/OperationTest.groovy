package agoodfriendalwayspayshisdebts.model.activity

import spock.lang.Specification

class OperationTest extends Specification {

  def "can create an operation with a type, data and the id of the event it is linked to"() {
    given:
    def eventId = UUID.randomUUID()
    def operation = new Operation(OperationType.EVENT_CREATION, "hello", eventId)

    expect:
    operation.id() != null
    operation.type() == OperationType.EVENT_CREATION
    operation.creationDate() != null
    operation.data() == "hello"
    operation.eventId() == eventId
  }

  def "can create an operation with a type and the id of the event it is linked to"() {
    given:
    def eventId = UUID.randomUUID()
    def operation = new Operation(OperationType.EVENT_CREATION, eventId)

    expect:
    operation.id() != null
    operation.type() == OperationType.EVENT_CREATION
    operation.creationDate() != null
    operation.data().empty
    operation.eventId() == eventId
  }
}
