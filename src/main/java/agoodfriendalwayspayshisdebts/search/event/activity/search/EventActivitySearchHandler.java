package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventActivity;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

import java.util.Optional;

public class EventActivitySearchHandler extends JongoSearchHandler<EventActivitySearch, Iterable<EventOperation>> {

  @Override
  protected Iterable<EventOperation> execute(EventActivitySearch search, Jongo jongo) {
    final int skip = (search.page() - 1) * PAGE_SIZE;
    final Optional<EventActivity> optionalEventActivity = Optional.ofNullable(
        jongo.getCollection("eventactivity_view")
            .findOne("{_id:#}", search.eventId)
            .projection("{operations:{$slice:[#,#]}}", skip, PAGE_SIZE)
            .as(EventActivity.class)
    );
    return optionalEventActivity.isPresent() ? optionalEventActivity.get().operations : null;
  }

  private final static int PAGE_SIZE = 10;
}
