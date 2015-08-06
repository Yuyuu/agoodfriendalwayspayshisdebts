package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.event.EventRepository;
import com.vter.infrastructure.persistence.mongo.MongoLinkRepositoryWithUuid;
import org.mongolink.MongoSession;

public class MongoLinkEventRepository extends MongoLinkRepositoryWithUuid<Event> implements EventRepository {

  protected MongoLinkEventRepository(MongoSession session) {
    super(session);
  }
}
