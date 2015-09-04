package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.command.expense.DeleteExpenseCommand
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class DeleteExpenseTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  def "can ask to delete an expense from an event"() {
    given:
    def action = new DeleteExpense(commandBus)
    commandBus.sendAndWaitResponse(_ as DeleteExpenseCommand) >> ExecutionResult.success(null)

    when:
    def dummyUuid = UUID.randomUUID().toString()
    def payload = action.delete(dummyUuid, dummyUuid)

    then:
    noExceptionThrown()
    payload.code() == 204
    payload.rawContent() == null
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
