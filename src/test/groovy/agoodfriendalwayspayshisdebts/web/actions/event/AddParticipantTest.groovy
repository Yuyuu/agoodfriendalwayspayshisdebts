package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.command.event.AddParticipantCommand
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class AddParticipantTest extends Specification {
  CommandBus bus = Mock(CommandBus)

  AddParticipant action

  def setup() {
    action = new AddParticipant(bus)
  }

  def "adds a participant"() {
    given:
    def command = Mock(AddParticipantCommand)
    def content = UUID.randomUUID()
    bus.sendAndWaitResponse(command) >> ExecutionResult.success(content)

    when:
    def payload = action.add(UUID.randomUUID().toString(), command)

    then:
    payload.code() == 201
    payload.rawContent() == content
  }

  def "propagates the error if any occurred"() {
    given:
    def command = Mock(AddParticipantCommand)
    bus.sendAndWaitResponse(command) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    action.add(UUID.randomUUID().toString(), command)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
