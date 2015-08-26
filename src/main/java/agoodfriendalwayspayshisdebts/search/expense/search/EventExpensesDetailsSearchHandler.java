package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class EventExpensesDetailsSearchHandler extends JongoSearchHandler<EventExpensesDetailsSearch, EventExpensesDetails> {

  @Override
  protected EventExpensesDetails execute(EventExpensesDetailsSearch search, Jongo jongo) {
    return jongo.getCollection("eventexpensesdetails_view")
        .findOne("{_id:#}", search.eventId)
        .as(EventExpensesDetails.class);
  }
}
