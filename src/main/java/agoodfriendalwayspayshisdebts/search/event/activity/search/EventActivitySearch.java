package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.search.Search;

import java.util.UUID;

public class EventActivitySearch extends Search<Iterable<EventOperation>> {

  public EventActivitySearch(UUID eventId, ActivityFilter filter, int page) {
    this.eventId = eventId;
    this.filter = filter;
    this.page = page;
  }

  public final UUID eventId;
  public final ActivityFilter filter;
  public final int page;
}
