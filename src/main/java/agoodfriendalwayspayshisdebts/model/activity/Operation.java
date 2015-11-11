package agoodfriendalwayspayshisdebts.model.activity;

import org.joda.time.DateTime;

import java.util.UUID;

public class Operation {

  private UUID id;
  private OperationType type;
  private DateTime creationDate;
  private String data;
  private UUID eventId;

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Operation() {}

  public Operation(OperationType type, String data, UUID eventId) {
    this.id = UUID.randomUUID();
    this.type = type;
    this.creationDate = DateTime.now();
    this.data = data;
    this.eventId = eventId;
  }

  public Operation(OperationType type, UUID eventId) {
    this(type, null, eventId);
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

  public UUID id() {
    return id;
  }

  public OperationType type() {
    return type;
  }

  public void eventId(UUID eventId) {
    this.eventId = eventId;
  }
}
