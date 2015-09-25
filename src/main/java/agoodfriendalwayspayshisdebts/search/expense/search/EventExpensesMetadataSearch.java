package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseMetadata;
import com.vter.search.Search;

import java.util.UUID;

public class EventExpensesMetadataSearch implements Search<Iterable<ExpenseMetadata>> {

  public EventExpensesMetadataSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
