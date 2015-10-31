package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.search.expense.metadata.search.ExpensesMetadataSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetExpensesMetadataTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetExpensesMetadata action = new GetExpensesMetadata(searchBus)

  def "can return the expenses metadata of an event"() {
    given:
    def expensesMetadata = Mock(Iterable)
    searchBus.sendAndWaitResponse(_ as ExpensesMetadataSearch) >> ExecutionResult.success(expensesMetadata)

    when:
    def result = action.get(UUID.randomUUID().toString()).get()

    then:
    result == expensesMetadata
  }
}
