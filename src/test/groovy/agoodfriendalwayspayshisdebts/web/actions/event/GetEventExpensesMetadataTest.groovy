package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.expense.metadata.search.EventExpensesMetadataSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventExpensesMetadataTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventExpensesMetadata action = new GetEventExpensesMetadata(searchBus)

  def "can return the expenses metadata of an event"() {
    given:
    def expensesMetadata = Mock(Iterable)
    searchBus.sendAndWaitResponse(_ as EventExpensesMetadataSearch) >> ExecutionResult.success(expensesMetadata)

    when:
    def result = action.get(UUID.randomUUID().toString()).get()

    then:
    result == expensesMetadata
  }
}
