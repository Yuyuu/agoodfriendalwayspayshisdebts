package agoodfriendalwayspayshisdebts.search.expense.search;

import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseMetadata;
import com.google.common.collect.Lists;
import com.vter.search.SearchHandler;

import java.util.UUID;

public class EventExpensesMetadataSearchHandler implements SearchHandler<EventExpensesMetadataSearch, Iterable<ExpenseMetadata>> {

  @Override
  public Iterable<ExpenseMetadata> execute(EventExpensesMetadataSearch eventExpensesMetadataSearch) {
    return Lists.newArrayList(
        new ExpenseMetadata(UUID.randomUUID(), "expense1"),
        new ExpenseMetadata(UUID.randomUUID(), "expense2"),
        new ExpenseMetadata(UUID.randomUUID(), "expense3")
    );
  }
}
