package agoodfriendalwayspayshisdebts.search.expense.metadata.search;

import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpenseMetadata;
import com.vter.search.Search;

import java.util.UUID;

public class ExpensesMetadataSearch extends Search<Iterable<ExpenseMetadata>> {

  public ExpensesMetadataSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
