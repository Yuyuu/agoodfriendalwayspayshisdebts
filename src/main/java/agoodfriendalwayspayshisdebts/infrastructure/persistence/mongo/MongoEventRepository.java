package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.event.EventRepository;
import com.vter.infrastructure.persistence.mongo.MongoRepositoryWithUuid;
import org.mongolink.MongoSession;

public class MongoEventRepository extends MongoRepositoryWithUuid<Event> implements EventRepository {

  protected MongoEventRepository(MongoSession session) {
    super(session);
  }
}
