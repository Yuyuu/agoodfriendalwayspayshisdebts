package agoodfriendalwayspayshisdebts.search.event.activity.model;

import com.google.common.collect.Lists;
import com.vter.search.PaginatedSearchResult;
import org.jongo.MongoCursor;

public class OperationsSearchResult extends PaginatedSearchResult<OperationDetails> {
  public OperationsSearchResult(MongoCursor<OperationDetails> cursor) {
    super(cursor.count(), Lists.newArrayList(cursor.iterator()));
  }
}
