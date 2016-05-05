package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter;
import agoodfriendalwayspayshisdebts.search.event.activity.search.EventActivitySearch;
import com.vter.search.Search;
import com.vter.search.SearchBus;
import com.vter.web.actions.BasePaginationAction;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class GetEventActivity extends BasePaginationAction {

  @Inject
  public GetEventActivity(SearchBus searchBus) {
    super(searchBus);
  }

  @Get("/events/:stringifiedEventUuid/activity?filter=:filter&per_page=:perPage&page=:page")
  public Payload get(String stringifiedEventUuid, String filter, int perPage, int page) {
    final UUID eventId = UUID.fromString(stringifiedEventUuid);
    final ActivityFilter activityFilter = ActivityFilter.parseFromString(filter);
    perPage = perPage <= 0 ? perPage(activityFilter) : perPage;
    page = page <= 0 ? 1 : page;

    final EventActivitySearch search = new EventActivitySearch(eventId, activityFilter);
    return paginationPayload(search.perPage(perPage).page(page));
  }

  @Override
  protected <TSearch extends Search<?>> String resourcePartialUri(TSearch tSearch) {
    assert tSearch.getClass() == EventActivitySearch.class;
    final EventActivitySearch search = (EventActivitySearch) tSearch;
    final String baseUri =  "/events/" + search.eventId.toString() + "/activity?per_page=" + search.perPage() + "&";
    return search.filter == ActivityFilter.ALL ? baseUri : baseUri + "filter=" + search.filter + "&";
  }

  private static int perPage(ActivityFilter filter) {
    switch (filter) {
      case EXPENSES:
      case PARTICIPANTS:
      case REMINDERS:
        return 3;
      default:
        return 10;
    }
  }
}
