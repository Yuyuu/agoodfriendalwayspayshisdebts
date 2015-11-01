package agoodfriendalwayspayshisdebts.search.event.results.search;

import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;
import com.vter.search.Search;

import java.util.UUID;

public class EventResultsSearch implements Search<Iterable<ParticipantResults>> {

  public EventResultsSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
