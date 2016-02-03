package agoodfriendalwayspayshisdebts.model.activity;

import com.vter.model.internal_event.InternalEvent;
import org.joda.time.DateTime;

import java.util.UUID;

public class OperationPerformedInternalEvent implements InternalEvent {

  public OperationPerformedInternalEvent(UUID eventId, OperationType operationType, String operationData) {
    this.eventId = eventId;
    this.operationType = operationType;
    this.operationData = operationData;
  }

  public final UUID eventId;
  public final OperationType operationType;
  public final DateTime creationDate = DateTime.now();
  public final String operationData;
}
