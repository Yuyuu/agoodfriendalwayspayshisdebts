package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.google.common.collect.Lists;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventActivitySearchHandler extends JongoSearchHandler<EventActivitySearch, Iterable<EventOperation>> {

  @Override
  protected Iterable<EventOperation> execute(EventActivitySearch search, Jongo jongo) {
    final int skip = (search.page() - 1) * PAGE_SIZE;
    return Lists.newArrayList(
        jongo.getCollection("eventactivity_view")
            .find("{eventId:#}", search.eventId)
            .skip(skip)
            .limit(PAGE_SIZE)
            .sort("{creationDate:-1}")
            .as(EventOperation.class)
            .iterator()
    );
  }

  private final static int PAGE_SIZE = 10;
}
