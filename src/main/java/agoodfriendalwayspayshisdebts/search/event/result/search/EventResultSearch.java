package agoodfriendalwayspayshisdebts.search.event.result.search;

import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.vter.search.Search;

import java.util.UUID;

public class EventResultSearch implements Search<Iterable<ParticipantResult>> {

  public EventResultSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
