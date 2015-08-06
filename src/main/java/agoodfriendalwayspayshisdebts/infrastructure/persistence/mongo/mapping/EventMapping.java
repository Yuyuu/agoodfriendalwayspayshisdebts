package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping;

import agoodfriendalwayspayshisdebts.model.event.Event;
import org.mongolink.domain.mapper.AggregateMap;

/* This is implicitly used by MongoLink */
@SuppressWarnings("unused")
public class EventMapping extends AggregateMap<Event> {

  @Override
  public void map() {
    id().onProperty(Event::getId).natural();
    property().onField("name");
    collection().onField("participants");
  }
}
