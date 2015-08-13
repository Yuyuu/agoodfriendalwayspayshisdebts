package agoodfriendalwayspayshisdebts.search.event.details.search;

import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventDetailsSearchHandler extends JongoSearchHandler<EventDetailsSearch, EventDetails> {

  @Override
  protected EventDetails execute(EventDetailsSearch search, Jongo jongo) {
    return jongo.getCollection("eventdetails_view").findOne("{_id:#}", search.eventId).as(EventDetails.class);
  }
}
