package agoodfriendalwayspayshisdebts.search.event.activity.model;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.UUID;

public class EventOperation {

  @MongoId
  public UUID id;
  public String type;
  public DateTime creationDate;
  public String data;
  public UUID eventId;

  private EventOperation() {}

  public static EventOperation forOperation(Operation operation) {
    final EventOperation eventOperation = new EventOperation();
    eventOperation.id = operation.id();
    eventOperation.type = operation.type().name();
    eventOperation.creationDate = operation.creationDate();
    eventOperation.data = operation.data();
    eventOperation.eventId = operation.eventId();
    return eventOperation;
  }
}
