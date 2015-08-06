package agoodfriendalwayspayshisdebts.model.event

import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class EventTest extends Specification {

  def "can create an event with a name and a list of participants"() {
    given:
    def event = new Event("cool event", [new Participant("kim", 1, null)])

    expect:
    event.id != null
    event.name() == "cool event"
    event.participants().first().name() == "kim"
  }
}
