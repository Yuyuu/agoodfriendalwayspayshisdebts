package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails;
import com.vter.search.Search;

import java.util.UUID;

public class EventExpensesDetailsSearch implements Search<EventExpensesDetails> {

  public EventExpensesDetailsSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
