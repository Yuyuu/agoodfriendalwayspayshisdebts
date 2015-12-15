package agoodfriendalwayspayshisdebts.search.expense.metadata.search;

import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpenseMetadata;
import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpensesMetadata;
import com.vter.search.JongoQueryBuilder;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

import java.util.Optional;

public class ExpensesMetadataSearchHandler extends JongoSearchHandler<ExpensesMetadataSearch, Iterable<ExpenseMetadata>> {

  @Override
  protected Iterable<ExpenseMetadata> execute(ExpensesMetadataSearch search, Jongo jongo) {
    final Optional<ExpensesMetadata> optionalExpensesMetadata = Optional.ofNullable(
        JongoQueryBuilder.create("expensesmetadata_view")
            .add("_id", "#", search.eventId)
            .findOne(jongo)
            .as(ExpensesMetadata.class)
    );
    return optionalExpensesMetadata.isPresent() ? optionalExpensesMetadata.get().metadata : null;
  }
}
