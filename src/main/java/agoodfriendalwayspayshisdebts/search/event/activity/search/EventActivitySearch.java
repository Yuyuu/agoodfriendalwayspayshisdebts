package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import com.vter.search.Search;

import java.util.UUID;

public class EventActivitySearch extends Search<Iterable<Operation>> {

  public EventActivitySearch(UUID eventId, ActivityFilter filter, int page) {
    this.eventId = eventId;
    this.filter = filter;
    this.page = page;
  }

  public final UUID eventId;
  public final ActivityFilter filter;
  public final int page;
}
