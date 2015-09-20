package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.command.email.SendReminderCommand
import com.vter.command.CommandBus
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class SendReminderTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  SendReminder action

  def setup() {
    action = new SendReminder(commandBus)
  }

  def "posts the command in the bus"() {
    given:
    commandBus.send(_ as SendReminderCommand) >> Mock(CompletableFuture)

    when:
    def payload = action.send(UUID.randomUUID().toString(), Mock(SendReminderCommand))

    then:
    payload.code() == 201
    payload.rawContent() instanceof CompletableFuture
  }
}
