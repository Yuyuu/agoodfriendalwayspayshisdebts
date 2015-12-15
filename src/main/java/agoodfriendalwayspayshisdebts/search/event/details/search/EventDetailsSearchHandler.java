package agoodfriendalwayspayshisdebts.search.event.details.search;

import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventDetailsSearchHandler extends JongoSearchHandler<EventDetailsSearch, EventDetails> {

  @Override
  protected EventDetails execute(EventDetailsSearch search, Jongo jongo) {
    return JongoQueryBuilder.create("eventdetails_view")
        .add("_id", "#", search.eventId)
        .findOne(jongo)
        .as(EventDetails.class);
  }
}
