package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;
import agoodfriendalwayspayshisdebts.search.event.results.search.EventResultsSearch;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.SearchBus;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetEventResults {

  @Inject
  public GetEventResults(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  @Get("/events/:stringifiedUuid/results")
  public Optional<Iterable<ParticipantResults>> retrieve(String stringifiedUuid) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<Iterable<ParticipantResults>> result = searchBus.sendAndWaitResponse(new EventResultsSearch(eventId));
    return Optional.ofNullable(result.data());
  }

  private final SearchBus searchBus;
}
