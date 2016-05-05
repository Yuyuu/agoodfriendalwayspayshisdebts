package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationDetails;
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationsSearchResult;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import com.vter.search.PaginationError;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

public class EventActivitySearchHandler extends JongoSearchHandler<EventActivitySearch, OperationsSearchResult> {

  @Override
  protected OperationsSearchResult execute(EventActivitySearch search, Jongo jongo) {
    assert search.perPage() > 0 && search.page() > 0;
    final JongoQueryBuilder queryBuilder = JongoQueryBuilder.create("operationdetails_view");
    queryBuilder.add("eventId", "#", search.eventId);
    applyTypeRestriction(queryBuilder, search.filter);
    final MongoCursor<OperationDetails> cursor = queryBuilder
        .find(jongo)
        .sort("{creationDate:-1}")
        .skip(search.skip())
        .limit(search.perPage())
        .as(OperationDetails.class);
    if (isBadPagination(search, cursor.count())) {
      throw new PaginationError();
    }
    return new OperationsSearchResult(cursor);
  }

  private static void applyTypeRestriction(JongoQueryBuilder queryBuilder, ActivityFilter filter) {
    String query;
    Object queryValues;
    assert filter.associatedOperationCount() >= 0;
    switch (filter.associatedOperationCount()) {
      case 0:
        return;
      case 1:
        query = "#";
        queryValues = filter.operationTypes().get(0);
        break;
      default:
        query = "{$in:#}";
        queryValues = filter.operationTypes();
        break;
    }
    queryBuilder.add("operationType", query, queryValues);
  }

  private static boolean isBadPagination(EventActivitySearch search, int totalCount) {
    final int totalPageCount = (totalCount + search.perPage() - 1) / search.perPage();
    return search.page() > totalPageCount;
  }
}
