package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import org.mongolink.domain.mapper.ComponentMap;

/* This is implicitly used by MongoLink */
@SuppressWarnings({"unused", "Duplicates"})
public class OperationMapping extends ComponentMap<Operation> {

  @Override
  public void map() {
    property().onField("id");
    property().onField("type");
    property().onField("creationDate");
    property().onField("data");
    property().onField("eventId");
  }
}
