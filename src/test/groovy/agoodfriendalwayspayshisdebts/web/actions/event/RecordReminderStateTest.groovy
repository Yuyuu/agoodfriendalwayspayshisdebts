package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.command.reminder.RecordReminderStateCommand
import com.vter.command.CommandBus
import spock.lang.Specification

class RecordReminderStateTest extends Specification {
  CommandBus bus = Mock(CommandBus)

  def "can record a reminder state"() {
    given:
    def action = new RecordReminderState(bus)

    when:
    def payload = action.post(new RecordReminderStateCommand())

    then:
    payload.code() == 200
  }
}
