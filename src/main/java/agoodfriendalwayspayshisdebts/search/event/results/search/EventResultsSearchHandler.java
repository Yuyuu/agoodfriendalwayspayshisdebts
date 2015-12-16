package agoodfriendalwayspayshisdebts.search.event.results.search;

import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventResultsSearchHandler extends JongoSearchHandler<EventResultsSearch, Iterable<ParticipantResults>> {

  @Override
  protected Iterable<ParticipantResults> execute(EventResultsSearch search, Jongo jongo) {
    final EventResults result = JongoQueryBuilder.create("eventresults_view")
        .add("_id", "#", search.eventId)
        .findOne(jongo)
        .as(EventResults.class);
    return result.participantsResults.values();
  }
}
