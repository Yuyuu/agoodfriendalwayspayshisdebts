package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpensesDetails
import agoodfriendalwayspayshisdebts.search.expense.details.search.ExpensesDetailsSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetExpensesDetailsTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetExpensesDetails action = new GetExpensesDetails(searchBus)

  def "can return the expenses details of an event"() {
    given:
    def expensesDetails = Mock(ExpensesDetails)
    searchBus.sendAndWaitResponse(_ as ExpensesDetailsSearch) >> ExecutionResult.success(expensesDetails)

    when:
    def result = action.getExpenses(UUID.randomUUID().toString(), 3, 2)

    then:
    result.get() == expensesDetails
  }
}
