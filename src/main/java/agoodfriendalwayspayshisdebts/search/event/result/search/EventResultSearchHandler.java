package agoodfriendalwayspayshisdebts.search.event.result.search;

import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventResultSearchHandler extends JongoSearchHandler<EventResultSearch, CalculationResult> {

  @Override
  protected CalculationResult execute(EventResultSearch search, Jongo jongo) {
    return jongo.getCollection("eventresult_view").findOne("{_id:#}", search.eventId).as(CalculationResult.class);
  }
}
