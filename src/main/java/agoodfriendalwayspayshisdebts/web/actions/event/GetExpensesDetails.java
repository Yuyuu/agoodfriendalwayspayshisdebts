package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpensesDetails;
import agoodfriendalwayspayshisdebts.search.expense.details.search.ExpensesDetailsSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetExpensesDetails {

  @Inject
  public GetExpensesDetails(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/expenses?skip=:skip&limit=:limit")
  public Optional<ExpensesDetails> getExpenses(String stringifiedUuid, int skip, int limit) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<ExpensesDetails> result = searchBus.sendAndWaitResponse(
        new ExpensesDetailsSearch(eventId).skip(skip).limit(limit)
    );
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
