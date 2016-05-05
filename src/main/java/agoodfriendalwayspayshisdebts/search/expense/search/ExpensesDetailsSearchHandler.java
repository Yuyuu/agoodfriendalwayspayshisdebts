package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesDetails;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesMetadata;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesSearchResult;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import com.vter.search.PaginationError;
import org.jongo.Jongo;

import java.util.Optional;
import java.util.UUID;

public class ExpensesDetailsSearchHandler extends JongoSearchHandler<ExpensesDetailsSearch, ExpensesSearchResult> {

  @Override
  protected ExpensesSearchResult execute(ExpensesDetailsSearch search, Jongo jongo) {
    final Optional<Integer> optionalExpenseCount = expenseCount(search.eventId, jongo);
    if (!optionalExpenseCount.isPresent()) {
      return null;
    }

    final int expenseCount = optionalExpenseCount.get();
    final int skip = search.skip();
    final int unskippedExpenseCount = unskippedExpenseCount(expenseCount, skip);

    final int limit = (unskippedExpenseCount >= search.perPage()) ? search.perPage() : unskippedExpenseCount;
    final int reverseSkip = skip + limit;

    if (limit <= 0 || reverseSkip < 0) {
      throw new PaginationError();
    }

    return JongoQueryBuilder.create("expensesdetails_view")
        .add("_id", "#", search.eventId)
        .findOne(jongo)
        .projection("{totalCount:1,items:{$slice:[#,#]}}", -reverseSkip, limit)
        .as(resultType(search.format));
  }

  private static Optional<Integer> expenseCount(UUID eventId, Jongo jongo) {
    final Optional<ExpenseCountQuery> optionalQueryResult = Optional.ofNullable(
        JongoQueryBuilder.create("expensesdetails_view")
            .add("_id", "#", eventId)
            .findOne(jongo)
            .projection("{_id:0,items:0}")
            .as(ExpenseCountQuery.class)
    );
    return optionalQueryResult.isPresent() ?
        Optional.of(optionalQueryResult.get().totalCount) : Optional.<Integer>empty();
  }

  private static int unskippedExpenseCount(int expenseCount, int skip) {
    int rawCount = expenseCount - skip;
    return rawCount > 0 ? rawCount : 0;
  }

  private static Class<? extends ExpensesSearchResult> resultType(String format) {
    return format.equals("meta") ? ExpensesMetadata.class : ExpensesDetails.class;
  }

  private static class ExpenseCountQuery {
    public int totalCount;
  }
}
