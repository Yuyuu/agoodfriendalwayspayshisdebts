package agoodfriendalwayspayshisdebts.search.event.activity.search;

import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.google.common.collect.Lists;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

public class HistorySearchHandler extends JongoSearchHandler<HistorySearch, Iterable<EventOperation>> {

  @Override
  protected Iterable<EventOperation> execute(HistorySearch search, Jongo jongo) {
    final int skip = (search.page() - 1) * PAGE_SIZE;
    final String operationTypeQuery = operationTypeQuery(search.type);
    return Lists.newArrayList(
        jongo.getCollection("eventactivity_view")
            .find("{eventId:#,type:" + operationTypeQuery + "}", search.eventId)
            .skip(skip)
            .limit(PAGE_SIZE)
            .sort("{creationDate:-1}")
            .as(EventOperation.class)
            .iterator()
    );
  }

  private static String operationTypeQuery(String type) {
    switch (type) {
      case "expenses":
        return "{$in:['" + OperationType.NEW_EXPENSE + "','" + OperationType.EXPENSE_DELETED + "']}";
      case "participants":
        return "{$in:['" + OperationType.NEW_PARTICIPANT + "','" + OperationType.PARTICIPANT_EDITED + "']}";
      default:
        return "'" + OperationType.NEW_REMINDER + "'";
    }
  }

  private final static int PAGE_SIZE = 3;
}
