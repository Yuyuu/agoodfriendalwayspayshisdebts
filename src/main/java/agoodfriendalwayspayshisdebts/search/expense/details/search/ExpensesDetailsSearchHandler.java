package agoodfriendalwayspayshisdebts.search.expense.details.search;

import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpensesDetails;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

import java.util.Optional;
import java.util.UUID;

public class ExpensesDetailsSearchHandler extends JongoSearchHandler<ExpensesDetailsSearch, ExpensesDetails> {

  @Override
  protected ExpensesDetails execute(ExpensesDetailsSearch search, Jongo jongo) {
    final Optional<Integer> optionalExpenseCount = expenseCount(search.eventId, jongo);
    if (!optionalExpenseCount.isPresent()) {
      return null;
    }

    final int expenseCount = optionalExpenseCount.get();
    final int unskippedExpenseCount = unskippedExpenseCount(expenseCount, search.skip());

    final int limit = (unskippedExpenseCount >= search.limit()) ? search.limit() : unskippedExpenseCount;
    final int reverseSkip = search.skip() + limit;

    return jongo.getCollection("expensesdetails_view")
        .findOne("{_id:#}", search.eventId)
        .projection("{expenseCount:1,expenses:{$slice:[#,#]}}", -reverseSkip, limit)
        .as(ExpensesDetails.class);
  }

  private static Optional<Integer> expenseCount(UUID eventId, Jongo jongo) {
    final Optional<ExpenseCountQuery> optionalQueryResult = Optional.ofNullable(
        jongo.getCollection("expensesdetails_view")
            .findOne("{_id:#}", eventId)
            .projection("{_id:0, expenses:0}")
            .as(ExpenseCountQuery.class)
    );
    return optionalQueryResult.isPresent() ?
        Optional.of(optionalQueryResult.get().expenseCount) : Optional.<Integer>empty();
  }

  private static int unskippedExpenseCount(int expenseCount, int skip) {
    int rawCount = expenseCount - skip;
    return rawCount > 0 ? rawCount : 0;
  }

  private static class ExpenseCountQuery {
    public int expenseCount;
  }
}
