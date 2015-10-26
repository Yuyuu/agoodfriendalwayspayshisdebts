package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.command.event.UpdateParticipantCommand
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class UpdateParticipantTest extends Specification {
  CommandBus bus = Mock(CommandBus)

  UpdateParticipant action

  def setup() {
    action = new UpdateParticipant(bus)
  }

  def "puts the command in the bus"() {
    given:
    def command = Mock(UpdateParticipantCommand)
    bus.sendAndWaitResponse(command) >> ExecutionResult.success(null)

    when:
    def payload = action.update(UUID.randomUUID().toString(), UUID.randomUUID().toString(), command)

    then:
    payload.code() == 200
  }

  def "propagates the error if any occurred"() {
    given:
    def command = Mock(UpdateParticipantCommand)
    bus.sendAndWaitResponse(command) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    action.update(UUID.randomUUID().toString(), UUID.randomUUID().toString(), command)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
