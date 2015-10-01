package agoodfriendalwayspayshisdebts.command.event

import spock.lang.Specification

class AddParticipantCommandHandlerTest extends Specification {

  def "returns null"() {
    expect:
    new AddParticipantCommandHandler().execute(new AddParticipantCommand()) == null
  }
}
