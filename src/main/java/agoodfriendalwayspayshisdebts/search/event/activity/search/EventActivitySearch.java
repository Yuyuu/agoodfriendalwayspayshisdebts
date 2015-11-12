package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.search.PaginatedSearch;

import java.util.UUID;

public class EventActivitySearch extends PaginatedSearch<Iterable<EventOperation>> {

  public EventActivitySearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
