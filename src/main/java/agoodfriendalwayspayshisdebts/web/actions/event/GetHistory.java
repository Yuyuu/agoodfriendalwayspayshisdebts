package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import agoodfriendalwayspayshisdebts.search.event.activity.search.HistorySearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetHistory {

  @Inject
  public GetHistory(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedEventUuid/history?type=:type&page=:page")
  public Optional<Iterable<EventOperation>> get(String stringifiedEventUuid, String type, int page) {
    final UUID eventId = UUID.fromString(stringifiedEventUuid);
    final ExecutionResult<Iterable<EventOperation>> result = searchBus.sendAndWaitResponse(
        new HistorySearch(eventId, type).page(page)
    );
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
