package agoodfriendalwayspayshisdebts.search.event.activity.model;

import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.UUID;

public class OperationDetails {
  @MongoId
  public UUID id;
  public OperationType operationType;
  public DateTime creationDate;
  public String operationData;
  public UUID eventId;

  private OperationDetails() {}

  public static OperationDetails forOperationPerformedInternalEvent(OperationPerformedInternalEvent internalEvent) {
    final OperationDetails operationDetails = new OperationDetails();
    operationDetails.id = UUID.randomUUID();
    operationDetails.operationType = internalEvent.operationType;
    operationDetails.creationDate = internalEvent.creationDate;
    operationDetails.operationData = internalEvent.operationData;
    operationDetails.eventId = internalEvent.eventId;
    return operationDetails;
  }
}
