package agoodfriendalwayspayshisdebts.search.event.result.search;

import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventResultSearchHandler extends JongoSearchHandler<EventResultSearch, Iterable<ParticipantResult>> {

  @Override
  protected Iterable<ParticipantResult> execute(EventResultSearch search, Jongo jongo) {
    final CalculationResult result =  jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", search.eventId)
        .as(CalculationResult.class);
    return result.participantsResults.values();
  }
}
