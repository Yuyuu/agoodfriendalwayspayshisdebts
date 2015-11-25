package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.google.common.collect.Lists;
import com.vter.search.JongoSearchHandler;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.UUID;

public class EventActivitySearchHandler extends JongoSearchHandler<EventActivitySearch, Iterable<EventOperation>> {

  @Override
  protected Iterable<EventOperation> execute(EventActivitySearch search, Jongo jongo) {
    final int skip = (search.page() - 1) * PAGE_SIZE;
    final Find findQuery = findQuery(jongo.getCollection("eventactivity_view"), search.eventId, search.filter);
    return Lists.newArrayList(
        findQuery.skip(skip).limit(PAGE_SIZE).sort("{creationDate:-1}").as(EventOperation.class).iterator()
    );
  }

  private static Find findQuery(MongoCollection view, UUID eventId, ActivityFilter filter) {
    assert filter.associatedOperationCount() >= 0;
    switch (filter.associatedOperationCount()) {
      case 0:
        return view.find("{eventId:#}", eventId);
      case 1:
        return view.find("{eventId:#,type:#}", eventId, filter.operationTypes().get(0));
      default:
        return view.find("{eventId:#,type:{$in:#}}", eventId, filter.operationTypes());
    }
  }

  private final static int PAGE_SIZE = 10;
}
