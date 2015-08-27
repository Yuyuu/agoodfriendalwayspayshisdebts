package agoodfriendalwayspayshisdebts.search.event.details.search;

import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
import com.vter.search.Search;

import java.util.UUID;

public class EventDetailsSearch implements Search<EventDetails> {

  public EventDetailsSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
