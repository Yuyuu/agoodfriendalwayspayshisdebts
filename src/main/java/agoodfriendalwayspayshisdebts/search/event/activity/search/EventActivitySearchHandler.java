package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import com.vter.search.MongoSearchHandler;
import org.mongolink.domain.criteria.Criteria;
import org.mongolink.domain.criteria.Order;
import org.mongolink.domain.criteria.Restrictions;

import java.util.List;
import java.util.stream.Collectors;

public class EventActivitySearchHandler extends MongoSearchHandler<EventActivitySearch, Iterable<Operation>> {

  @Override
  public Iterable<Operation> execute(EventActivitySearch search) {
    final int pageSize = pageSize(search.filter);
    final int skip = (search.page - 1) * pageSize;
    final Criteria<Operation> criteria = session().createCriteria(Operation.class);
    criteria.add(Restrictions.equals("eventId", search.eventId));
    applyTypeRestriction(criteria, search.filter);
    criteria.sort("creationDate", Order.DESCENDING);
    criteria.skip(skip);
    criteria.limit(pageSize);
    return criteria.list();
  }

  private static void applyTypeRestriction(Criteria criteria, ActivityFilter filter) {
    assert filter.associatedOperationCount() >= 0;
    switch (filter.associatedOperationCount()) {
      case 0:
        return;
      case 1:
        criteria.add(Restrictions.equals("type", filter.operationTypes().get(0).name()));
        break;
      default:
        final List<String> operationTypes = filter.operationTypes().stream().map(Enum::name).collect(Collectors.toList());
        criteria.add(Restrictions.in("type", operationTypes));
        break;
    }
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
