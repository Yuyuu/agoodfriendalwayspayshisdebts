package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.search.PaginatedSearch;

import java.util.UUID;

public class EventActivitySearch extends PaginatedSearch<Iterable<EventOperation>> {

  public EventActivitySearch(UUID eventId, ActivityFilter filter) {
    this.eventId = eventId;
    this.filter = filter;
  }

  public final UUID eventId;
  public final ActivityFilter filter;
}
