package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import org.mongolink.domain.mapper.AggregateMap;

/* This is implicitly used by MongoLink */
@SuppressWarnings({"unused", "Duplicates"})
public class OperationMapping extends AggregateMap<Operation> {

  @Override
  public void map() {
    id().onProperty(Operation::getId).natural();
    property().onField("type");
    property().onField("creationDate");
    property().onField("data");
    property().onField("eventId");
  }
}
