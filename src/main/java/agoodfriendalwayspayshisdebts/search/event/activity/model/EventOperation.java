package agoodfriendalwayspayshisdebts.search.event.activity.model;

import agoodfriendalwayspayshisdebts.model.activity.Operation;

public class EventOperation {

  public String id;
  public String type;
  public String creationDate;
  public String eventId;

  private EventOperation() {}

  public static EventOperation forOperation(Operation operation) {
    final EventOperation eventOperation = new EventOperation();
    eventOperation.id = operation.id().toString();
    eventOperation.type = operation.type().name();
    eventOperation.creationDate = operation.creationDate().toString();
    eventOperation.eventId = operation.eventId().toString();
    return eventOperation;
  }
}
