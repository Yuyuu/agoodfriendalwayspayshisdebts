package agoodfriendalwayspayshisdebts.search.expense.details.search;

import agoodfriendalwayspayshisdebts.search.expense.details.model.EventExpensesDetails;
import com.vter.search.PaginatedSearch;

import java.util.UUID;

public class EventExpensesDetailsSearch extends PaginatedSearch<EventExpensesDetails> {

  public EventExpensesDetailsSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
