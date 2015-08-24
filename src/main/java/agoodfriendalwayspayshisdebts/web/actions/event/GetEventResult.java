package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.search.EventResultSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetEventResult {

  @Inject
  public GetEventResult(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/result")
  public Optional<CalculationResult> retrieve(String stringifiedUuid) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<CalculationResult> result = searchBus.sendAndWaitResponse(new EventResultSearch(eventId));
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
