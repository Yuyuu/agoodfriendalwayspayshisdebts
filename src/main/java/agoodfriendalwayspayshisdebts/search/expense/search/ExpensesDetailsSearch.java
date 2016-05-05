package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesSearchResult;
import com.vter.search.Search;

import java.util.UUID;

public class ExpensesDetailsSearch extends Search<ExpensesSearchResult> {

  public ExpensesDetailsSearch(UUID eventId, String format) {
    this.eventId = eventId;
    this.format = format;
  }

  public final UUID eventId;
  public final String format;
}
