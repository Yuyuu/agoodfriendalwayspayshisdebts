package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.search.expense.search.ExpensesDetailsSearch;
import com.vter.search.Search;
import com.vter.search.SearchBus;
import com.vter.web.actions.BasePaginationAction;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetExpensesDetails extends BasePaginationAction {

  @Inject
  public GetExpensesDetails(SearchBus searchBus) {
    super(searchBus);
  }

  @Get("/events/:stringifiedUuid/expenses?format=:format&per_page=:perPage&page=:page")
  public Payload getExpenses(String stringifiedUuid, String format, int perPage, int page) {
    final UUID eventId = UUID.fromString(stringifiedUuid);
    format = Optional.ofNullable(format).orElse("");
    perPage = perPage <= 0 ? perPage(format) : perPage;
    page = page <= 0 ? 1 : page;

    final ExpensesDetailsSearch search = new ExpensesDetailsSearch(eventId, format);
    return paginationPayload(search.perPage(perPage).page(page));
  }

  @Override
  protected <TSearch extends Search<?>> String resourcePartialUri(TSearch tSearch) {
    assert tSearch.getClass() == ExpensesDetailsSearch.class;
    final ExpensesDetailsSearch search = (ExpensesDetailsSearch) tSearch;
    final String baseUri =  "/events/" + search.eventId.toString() + "/expenses?per_page=" + search.perPage() + "&";
    return search.format.isEmpty() ? baseUri : baseUri + "format=" + search.format + "&";
  }

  private static int perPage(String format) {
    return format.equals("meta") ? 10 : 4;
  }
}
