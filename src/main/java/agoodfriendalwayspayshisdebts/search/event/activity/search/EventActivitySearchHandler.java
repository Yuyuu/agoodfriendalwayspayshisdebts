package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationDetails;
import com.google.common.collect.Lists;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import org.jongo.Find;
import org.jongo.Jongo;

public class EventActivitySearchHandler extends JongoSearchHandler<EventActivitySearch, Iterable<OperationDetails>> {

  @Override
  protected Iterable<OperationDetails> execute(EventActivitySearch search, Jongo jongo) {
    final int pageSize = pageSize(search.filter);
    final int skip = (search.page - 1) * pageSize;
    final JongoQueryBuilder queryBuilder = JongoQueryBuilder.create("operationdetails_view");
    queryBuilder.add("eventId", "#", search.eventId);
    applyTypeRestriction(queryBuilder, search.filter);
    final Find findQuery = queryBuilder.find(jongo);
    return Lists.newArrayList(
        findQuery.sort("{creationDate:-1}").skip(skip).limit(pageSize).as(OperationDetails.class).iterator()
    );
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

  private static int pageSize(ActivityFilter filter) {
    switch (filter) {
      case EXPENSES:
      case PARTICIPANTS:
      case REMINDERS:
        return 3;
      default:
        return 10;
    }
  }
}
