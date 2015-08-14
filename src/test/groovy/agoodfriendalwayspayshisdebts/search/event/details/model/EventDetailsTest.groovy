package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class EventDetailsTest extends Specification {

  def "can create from an event"() {
    given:
    def event = new Event("event", [new Participant("lea", 1, null)])
    event.expenses().add(new Expense("label", UUID.randomUUID(), 10, []))

    when:
    def eventDetails = EventDetails.fromEvent(event)

    then:
    eventDetails.id == event.id
    eventDetails.name == "event"
    eventDetails.participants[0].name == "lea"
    eventDetails.expenses[0].label == "label"
    eventDetails.expenses[0].amount == 10
  }
}
