package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import agoodfriendalwayspayshisdebts.search.event.activity.search.EventActivitySearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetEventActivity {

  @Inject
  public GetEventActivity(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedEventUuid/activity?page=:page")
  public Optional<Iterable<EventOperation>> get(String stringifiedEventUuid, int page) {
    final UUID eventId = UUID.fromString(stringifiedEventUuid);
    final ExecutionResult<Iterable<EventOperation>> result = searchBus.sendAndWaitResponse(
        new EventActivitySearch(eventId).page(page)
    );
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
