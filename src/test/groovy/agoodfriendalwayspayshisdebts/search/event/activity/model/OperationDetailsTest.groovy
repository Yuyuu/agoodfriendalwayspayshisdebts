package agoodfriendalwayspayshisdebts.search.event.activity.model

import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import spock.lang.Specification

class OperationDetailsTest extends Specification {

  def "is based on an operation performed internal event"() {
    given:
    def eventId = UUID.randomUUID()
    def internalEvent = new OperationPerformedInternalEvent(eventId, OperationType.NEW_EXPENSE, "hello")

    expect:
    def operationDetails = OperationDetails.forOperationPerformedInternalEvent(internalEvent)
    operationDetails.eventId == eventId
    operationDetails.operationType == OperationType.NEW_EXPENSE
    operationDetails.creationDate == internalEvent.creationDate
    operationDetails.id != null
    operationDetails.operationData == internalEvent.operationData
  }
}
