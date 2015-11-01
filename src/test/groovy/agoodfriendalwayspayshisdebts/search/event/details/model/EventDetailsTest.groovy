package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class EventDetailsTest extends Specification {

  def "can create for an event"() {
    given:
    def event = new Event("event", [new Participant("lea", 1, null)])

    when:
    def eventDetails = EventDetails.forEvent(event)

    then:
    eventDetails.id == event.id
    eventDetails.name == "event"
    eventDetails.participants[0].name == "lea"
  }
}
