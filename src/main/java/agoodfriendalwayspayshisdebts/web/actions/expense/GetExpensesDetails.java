package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.search.expense.details.search.ExpensesDetailsSearch;
import agoodfriendalwayspayshisdebts.search.expense.metadata.search.ExpensesMetadataSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.Search;
import com.vter.search.SearchBus;
import com.vter.web.actions.BaseAction;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetExpensesDetails extends BaseAction {

  @Inject
  public GetExpensesDetails(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/expenses?format=:format&skip=:skip&limit=:limit")
  public Optional<?> getExpenses(String stringifiedUuid, String format, int skip, int limit) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final Optional<String> optionalFormat = Optional.ofNullable(format);
    final ExecutionResult<?> result = searchBus.sendAndWaitResponse(
        search(optionalFormat, eventId).skip(skip).limit(limit)
    );
    return getOptionalDataOrFail(result);
  }

  private static Search<?> search(Optional<String> format, UUID eventId) {
    if (format.isPresent()) {
      return format.get().equals("meta") ? new ExpensesMetadataSearch(eventId) : new ExpensesDetailsSearch(eventId);
    }
    return new ExpensesDetailsSearch(eventId);
  }

  private final SearchBus searchBus;
}
