package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.expense.details.model.EventExpensesDetails
import agoodfriendalwayspayshisdebts.search.expense.details.search.EventExpensesDetailsSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventExpensesDetailsTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventExpensesDetails action = new GetEventExpensesDetails(searchBus)

  def "can return the expenses details of an event"() {
    given:
    def expensesDetails = Mock(EventExpensesDetails)
    searchBus.sendAndWaitResponse(_ as EventExpensesDetailsSearch) >> ExecutionResult.success(expensesDetails)

    when:
    def result = action.getExpenses(UUID.randomUUID().toString(), 3, 2)

    then:
    result.get() == expensesDetails
  }
}
