package agoodfriendalwayspayshisdebts.web.action.event

import agoodfriendalwayspayshisdebts.command.event.CreateEventCommand
import com.vter.command.CommandBus
import com.vter.infrastructure.bus.ExecutionResult
import spock.lang.Specification

class CreateEventTest extends Specification {

  CommandBus bus = Mock(CommandBus)

  def "can ask to create an event"() {
    given:
    def action = new CreateEvent(bus)
    def command = new CreateEventCommand()
    def id = UUID.randomUUID()
    bus.sendAndWaitResponse(command) >> ExecutionResult.success(id)

    when:
    def result = action.create(command)

    then:
    result == id
  }

  def "propagates the error if any occurred"() {
    given:
    def action = new CreateEvent(bus)
    def command = new CreateEventCommand()
    bus.sendAndWaitResponse(command) >> ExecutionResult.error(new RuntimeException("error"))

    when:
    action.create(command)

    then:
    def error = thrown(RuntimeException)
    error.message == "error"
  }
}
