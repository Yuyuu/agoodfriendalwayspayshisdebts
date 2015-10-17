package agoodfriendalwayspayshisdebts.web.actions.event

import spock.lang.Specification

class UpdateParticipantTest extends Specification {
  UpdateParticipant action

  def setup() {
    action = new UpdateParticipant()
  }

  def "pretends the participant has been updated"() {
    when:
    def payload = action.update(null, null)

    then:
    payload.code() == 200
  }
}
