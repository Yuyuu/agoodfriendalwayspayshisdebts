package agoodfriendalwayspayshisdebts.search.expense.model;

import com.vter.search.PaginatedSearchResult;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;

public abstract class ExpensesSearchResult<TItem> extends PaginatedSearchResult<TItem> {
  @MongoId
  public UUID eventId;

  protected ExpensesSearchResult() {}

  public ExpensesSearchResult(int totalCount, List<TItem> items) {
    super(totalCount, items);
  }
}
