package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.search.EventActivitySearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import com.vter.web.actions.BaseAction;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetEventActivity extends BaseAction {

  @Inject
  public GetEventActivity(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedEventUuid/activity?filter=:filter&page=:page")
  public Optional<Iterable<Operation>> get(String stringifiedEventUuid, String filter, int page) {
    final UUID eventId = UUID.fromString(stringifiedEventUuid);
    final ActivityFilter activityFilter = ActivityFilter.parseFromString(filter);
    final ExecutionResult<Iterable<Operation>> result = searchBus.sendAndWaitResponse(
        new EventActivitySearch(eventId, activityFilter, page)
    );
    return getOptionalDataOrFail(result);
  }

  private final SearchBus searchBus;
}
