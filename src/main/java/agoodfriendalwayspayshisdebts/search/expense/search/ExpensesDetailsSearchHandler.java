package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.model.expense.State;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseMetadata;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesSearchResult;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

public class ExpensesDetailsSearchHandler extends JongoSearchHandler<ExpensesDetailsSearch, ExpensesSearchResult> {

  @Override
  protected ExpensesSearchResult execute(ExpensesDetailsSearch search, Jongo jongo) {
    assert search.perPage() > 0 && search.page() > 0 && search.format != null;
    final JongoQueryBuilder jongoQueryBuilder = JongoQueryBuilder.create("expensesdetails_view")
        .add("eventId", "#", search.eventId);
    applyStateRestrictionIfNeeded(jongoQueryBuilder, search.format);
    final MongoCursor<?> cursor = jongoQueryBuilder
        .find(jongo)
        .sort("{creationDate:-1}")
        .skip(search.skip())
        .limit(search.perPage())
        .as(resultType(search.format));
    return new ExpensesSearchResult<>(cursor);
  }

  private void applyStateRestrictionIfNeeded(JongoQueryBuilder jongoQueryBuilder, String format) {
    if (format.equals("meta")) {
      jongoQueryBuilder.add("state", "#", State.ADDED);
    }
  }

  private static Class<?> resultType(String format) {
    return format.equals("meta") ? ExpenseMetadata.class : ExpenseDetails.class;
  }
}
