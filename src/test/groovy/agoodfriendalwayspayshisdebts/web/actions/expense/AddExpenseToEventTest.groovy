package agoodfriendalwayspayshisdebts.web.actions.expense

import agoodfriendalwayspayshisdebts.command.expense.AddExpenseCommand
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class AddExpenseToEventTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  def "can ask to add an expense to an event"() {
    given:
    def action = new AddExpenseToEvent(commandBus)
    def eventId = UUID.randomUUID()
    def purchaserId = UUID.randomUUID()
    def command = new AddExpenseCommand(
        label: "label",
        purchaserUuid: purchaserId.toString(),
        amount: 1,
        participantsUuids: [purchaserId.toString()],
        description: "description"
    )
    commandBus.sendAndWaitResponse(command) >> ExecutionResult.success(Void)

    when:
    def payload = action.add(eventId.toString(), command)

    then:
    noExceptionThrown()
    payload.code() == 201
  }

  def "propagates the error if any occurred"() {
    given:
    def action = new AddExpenseToEvent(commandBus)
    def command = new AddExpenseCommand()
    commandBus.sendAndWaitResponse(command) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    action.add(UUID.randomUUID().toString(), command)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
