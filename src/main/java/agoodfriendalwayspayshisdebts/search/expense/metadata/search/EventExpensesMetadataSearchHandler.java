package agoodfriendalwayspayshisdebts.search.expense.metadata.search;

import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpenseMetadata;
import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpensesMetadata;
import com.vter.search.JongoSearchHandler;
import org.jongo.Jongo;

import java.util.Optional;

public class EventExpensesMetadataSearchHandler extends JongoSearchHandler<EventExpensesMetadataSearch, Iterable<ExpenseMetadata>> {

  @Override
  protected Iterable<ExpenseMetadata> execute(EventExpensesMetadataSearch search, Jongo jongo) {
    final Optional<ExpensesMetadata> optionalExpensesMetadata = Optional.ofNullable(
        jongo.getCollection("expensesmetadata_view").findOne("{_id:#}", search.eventId).as(ExpensesMetadata.class)
    );
    return optionalExpensesMetadata.isPresent() ? optionalExpensesMetadata.get().metadata : null;
  }
}
