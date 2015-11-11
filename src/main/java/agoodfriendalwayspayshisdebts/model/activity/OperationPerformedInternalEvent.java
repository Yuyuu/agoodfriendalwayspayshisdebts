package agoodfriendalwayspayshisdebts.model.activity;

import com.vter.model.internal_event.InternalEvent;

import java.util.UUID;

public class OperationPerformedInternalEvent implements InternalEvent {

  public OperationPerformedInternalEvent(UUID eventId, UUID operationId) {
    this.eventId = eventId;
    this.operationId = operationId;
  }

  public final UUID eventId;
  public final UUID operationId;
}
