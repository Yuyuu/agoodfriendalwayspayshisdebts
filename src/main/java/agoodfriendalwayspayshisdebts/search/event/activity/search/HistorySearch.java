package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.search.PaginatedSearch;

import java.util.UUID;

public class HistorySearch extends PaginatedSearch<Iterable<EventOperation>> {

  public HistorySearch(UUID eventId, String type) {
    this.eventId = eventId;
    this.type = type;
  }

  public final UUID eventId;
  public final String type;
}
