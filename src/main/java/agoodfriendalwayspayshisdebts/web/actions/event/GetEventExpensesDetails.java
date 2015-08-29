package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails;
import agoodfriendalwayspayshisdebts.search.expense.search.EventExpensesDetailsSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Resource
public class GetEventExpensesDetails {

  @Inject
  public GetEventExpensesDetails(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/expenses?skip=:skip&limit=:limit")
  public EventExpensesDetails getExpenses(String stringifiedUuid, int skip, int limit) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<EventExpensesDetails> result = searchBus.sendAndWaitResponse(
        new EventExpensesDetailsSearch(eventId).skip(skip).limit(limit)
    );
    return Optional.ofNullable(result.data()).orElseGet(emptyEventExpensesDetails(eventId));
  }

  private static Supplier<EventExpensesDetails> emptyEventExpensesDetails(UUID eventId) {
    return () -> new EventExpensesDetails(eventId);
  }

  private final SearchBus searchBus;
}
