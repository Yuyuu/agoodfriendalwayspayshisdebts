package agoodfriendalwayspayshisdebts.search.expense.model;

import com.google.common.collect.Lists;
import com.vter.search.PaginatedSearchResult;
import org.jongo.MongoCursor;

public class ExpensesSearchResult<TItem> extends PaginatedSearchResult<TItem> {
  public ExpensesSearchResult(MongoCursor<TItem> cursor) {
    super(cursor.count(), Lists.newArrayList(cursor.iterator()));
  }
}
