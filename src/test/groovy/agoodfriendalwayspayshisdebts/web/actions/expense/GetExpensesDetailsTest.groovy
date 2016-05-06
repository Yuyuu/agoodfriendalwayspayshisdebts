package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.search.expense.model.ExpensesSearchResult
import agoodfriendalwayspayshisdebts.search.expense.search.ExpensesDetailsSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetExpensesDetailsTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetExpensesDetails action = new GetExpensesDetails(searchBus)

  def "can return the expenses details of an event"() {
    given:
    def result = Mock(ExpensesSearchResult)
    result.totalCount = 3
    result.items = []
    searchBus.sendAndWaitResponse(_ as ExpensesDetailsSearch) >> ExecutionResult.success(result)

    when:
    def id = UUID.randomUUID().toString()
    def payload = action.getExpenses(id, null, 1, 1)

    then:
    payload.rawContent().totalCount == 3
    payload.rawContent().items == []
    payload.headers().get("Link") == "</events/$id/expenses?per_page=1&page=2>; rel=\"next\", </events/$id/expenses?per_page=1&page=3>; rel=\"last\"" as String
  }

  def "can return the expenses metadata of an event"() {
    given:
    def result = Mock(ExpensesSearchResult)
    result.totalCount = 3
    result.items = []
    searchBus.sendAndWaitResponse(_ as ExpensesDetailsSearch) >> ExecutionResult.success(result)

    when:
    def id = UUID.randomUUID().toString()
    def payload = action.getExpenses(id, "meta", 1, 1)

    then:
    payload.rawContent().totalCount == 3
    payload.rawContent().items == []
    payload.headers().get("Link") == "</events/$id/expenses?per_page=1&format=meta&page=2>; rel=\"next\", </events/$id/expenses?per_page=1&format=meta&page=3>; rel=\"last\"" as String
  }

  def "has default per page and page values"() {
    when:
    action.getExpenses(UUID.randomUUID().toString(), format, perPage, page)

    then:
    1 * searchBus.sendAndWaitResponse({it.perPage() == finalPerPage && it.page() == finalPage})

    where:
    format | perPage | page | finalPerPage | finalPage
    null   | 0       | 0    | 10           | 1
    "meta" | -1      | 1    | 20           | 1
    null   | 3       | 2    | 3            | 2
    "meta" | 3       | 2    | 3            | 2
    "meta" | 3       | 0    | 3            | 1
    null   | 3       | -2   | 3            | 1
  }
}
