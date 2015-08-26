package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.command.expense.AddExpenseCommand
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class AddExpenseToEventTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  def "can ask to add an expense to an event"() {
    given:
    def expenseDetails = Mock(ExpenseDetails)
    def action = new AddExpenseToEvent(commandBus)
    def command = Mock(AddExpenseCommand)
    commandBus.sendAndWaitResponse(command) >> ExecutionResult.success(expenseDetails)

    when:
    def payload = action.add(UUID.randomUUID().toString(), command)

    then:
    noExceptionThrown()
    payload.code() == 201
    payload.rawContent() == expenseDetails
  }

  def "propagates the error if any occurred"() {
    given:
    def action = new AddExpenseToEvent(commandBus)
    def command = Mock(AddExpenseCommand)
    commandBus.sendAndWaitResponse(command) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    action.add(UUID.randomUUID().toString(), command)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
