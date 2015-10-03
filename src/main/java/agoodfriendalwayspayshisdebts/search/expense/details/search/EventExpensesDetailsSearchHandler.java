package agoodfriendalwayspayshisdebts.search.expense.details.search;

import agoodfriendalwayspayshisdebts.search.expense.details.model.EventExpensesDetails;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

import java.util.Optional;
import java.util.UUID;

public class EventExpensesDetailsSearchHandler extends JongoSearchHandler<EventExpensesDetailsSearch, EventExpensesDetails> {

  @Override
  protected EventExpensesDetails execute(EventExpensesDetailsSearch search, Jongo jongo) {
    final int expenseCount = expenseCount(search.eventId, jongo);
    final int remainingExpenseCount = expenseCount - search.skip();

    if (remainingExpenseCount < 1) {
      return emptyEventExpensesDetails(search.eventId);
    }

    final int limit = (remainingExpenseCount >= search.limit()) ? search.limit() : remainingExpenseCount;
    final int reverseSkip = search.skip() + limit;

    return jongo.getCollection("eventexpensesdetails_view")
        .findOne("{_id:#}", search.eventId)
        .projection("{expenseCount:1,expenses:{$slice:[#,#]}}", -reverseSkip, limit)
        .as(EventExpensesDetails.class);
  }

  private static int expenseCount(UUID eventId, Jongo jongo) {
    final Optional<ExpenseCountQuery> optionalQuery = Optional.ofNullable(
        jongo.getCollection("eventexpensesdetails_view")
            .findOne("{_id:#}", eventId)
            .projection("{_id:0, expenses:0}")
            .as(ExpenseCountQuery.class)
    );
    final ExpenseCountQuery query = optionalQuery.orElseGet(ExpenseCountQuery::empty);
    return query.expenseCount;
  }

  private static EventExpensesDetails emptyEventExpensesDetails(UUID eventId) {
    return new EventExpensesDetails(eventId);
  }

  private static class ExpenseCountQuery {
    public int expenseCount;

    public static ExpenseCountQuery empty() {
      return new ExpenseCountQuery();
    }
  }
}
