package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.command.expense.DeleteExpenseCommand
import agoodfriendalwayspayshisdebts.model.expense.Expense
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class DeleteExpenseTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  def "can ask to delete an expense from an event"() {
    given:
    def action = new DeleteExpense(commandBus)
    def result = Mock(Expense)
    commandBus.sendAndWaitResponse(_ as DeleteExpenseCommand) >> ExecutionResult.success(result)

    when:
    def dummyUuid = UUID.randomUUID().toString()
    def expense = action.delete(dummyUuid, dummyUuid)

    then:
    noExceptionThrown()
    expense == result
  }

  def "propagates the error if any occurred"() {
    given:
    def action = new DeleteExpense(commandBus)
    commandBus.sendAndWaitResponse(_ as DeleteExpenseCommand) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    def dummyUuid = UUID.randomUUID().toString()
    action.delete(dummyUuid, dummyUuid)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
