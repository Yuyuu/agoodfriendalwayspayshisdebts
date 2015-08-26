package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails
import agoodfriendalwayspayshisdebts.search.expense.search.EventExpensesDetailsSearch
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
    def optionalEventResult = action.getExpenses(UUID.randomUUID().toString())

    then:
    optionalEventResult == expensesDetails
  }

  def "creates an empty expenses details on the fly if none exists for the event"() {
    given:
    searchBus.sendAndWaitResponse(_ as EventExpensesDetailsSearch) >> ExecutionResult.success(null)

    when:
    def optionalEventResult = action.getExpenses(UUID.randomUUID().toString())

    then:
    optionalEventResult.expenses.empty
  }
}
