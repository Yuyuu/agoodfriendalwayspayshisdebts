package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationsSearchResult;
import com.vter.search.Search;

import java.util.UUID;

public class EventActivitySearch extends Search<OperationsSearchResult> {

  public EventActivitySearch(UUID eventId, ActivityFilter filter) {
    this.eventId = eventId;
    this.filter = filter;
  }

  public final UUID eventId;
  public final ActivityFilter filter;
}
