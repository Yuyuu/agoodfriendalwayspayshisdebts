package agoodfriendalwayspayshisdebts.model.activity;

import com.vter.model.BaseAggregateWithUuid;
import org.joda.time.DateTime;

import java.util.UUID;

public class Operation extends BaseAggregateWithUuid {

  private OperationType type;
  private DateTime creationDate;
  private String data;
  private UUID eventId;

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Operation() {}

  public Operation(OperationType type, String data, UUID eventId) {
    this.type = type;
    this.creationDate = DateTime.now();
    this.data = data;
    this.eventId = eventId;
  }

  public DateTime creationDate() {
    return creationDate;
  }

  public String data() {
    return data;
  }

  public UUID eventId() {
    return eventId;
  }

  public OperationType type() {
    return type;
  }

  public void eventId(UUID eventId) {
    this.eventId = eventId;
  }
}
