package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpenseMetadata;
import agoodfriendalwayspayshisdebts.search.expense.metadata.search.ExpensesMetadataSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetExpensesMetadata {

  @Inject
  public GetExpensesMetadata(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/expenses/meta")
  public Optional<Iterable<ExpenseMetadata>> get(String stringifiedUuid) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<Iterable<ExpenseMetadata>> result = searchBus.sendAndWaitResponse(
        new ExpensesMetadataSearch(eventId)
    );
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
