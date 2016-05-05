package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationsSearchResult
import agoodfriendalwayspayshisdebts.search.event.activity.search.EventActivitySearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventActivityTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventActivity action

  def setup() {
    action = new GetEventActivity(searchBus)
  }

  def "can return the operations of an event"() {
    given:
    def result = Mock(OperationsSearchResult)
    result.totalCount = 3
    result.items = []
    searchBus.sendAndWaitResponse({it.filter == ActivityFilter.ALL}) >> ExecutionResult.success(result)

    when:
    def id = UUID.randomUUID().toString()
    def payload = action.get(id, null, 1, 1)

    then:
    payload.rawContent().totalCount == 3
    payload.rawContent().items == []
    payload.headers().get("Link") == "</events/$id/activity?per_page=1&page=2>; rel=\"next\", </events/$id/activity?per_page=1&page=3>; rel=\"last\"" as String
  }

  def "add the filter to the pagination link"() {
    given:
    def result = Mock(OperationsSearchResult)
    result.totalCount = 3
    result.items = []
    searchBus.sendAndWaitResponse(_ as EventActivitySearch) >> ExecutionResult.success(result)

    when:
    def id = UUID.randomUUID().toString()
    def payload = action.get(id, "reminders", 1, 1)

    then:
    payload.headers().get("Link") == "</events/$id/activity?per_page=1&filter=reminders&page=2>; rel=\"next\", </events/$id/activity?per_page=1&filter=reminders&page=3>; rel=\"last\"" as String
  }

  def "has default per page and page values"() {
    when:
    action.get(UUID.randomUUID().toString(), filter, perPage, page)

    then:
    1 * searchBus.sendAndWaitResponse({it.perPage() == finalPerPage && it.page() == finalPage})

    where:
    filter         | perPage | page | finalPerPage | finalPage
    null           | 0       | 0    | 10           | 1
    "expenses"     | -1      | 1    | 3            | 1
    "participants" | 0       | 1    | 3            | 1
    "reminders"    | 0       | 1    | 3            | 1
    null           | 3       | 2    | 3            | 2
    "expenses"     | 3       | 2    | 3            | 2
    "expenses"     | 3       | 0    | 3            | 1
    null           | 3       | -2   | 3            | 1
  }
}
