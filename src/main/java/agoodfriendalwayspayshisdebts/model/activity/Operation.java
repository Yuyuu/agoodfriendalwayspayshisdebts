package agoodfriendalwayspayshisdebts.model.activity;

import org.joda.time.DateTime;

import java.util.UUID;

public class Operation {

  private UUID id;
  private OperationType type;
  private DateTime creationDate;
  private UUID eventId;

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Operation() {}

  public Operation(OperationType type, UUID eventId) {
    this.id = UUID.randomUUID();
    this.type = type;
    this.creationDate = DateTime.now();
    this.eventId = eventId;
  }

  public DateTime creationDate() {
    return creationDate;
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
