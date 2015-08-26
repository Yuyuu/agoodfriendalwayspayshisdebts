package agoodfriendalwayspayshisdebts.search.event.details.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventDetailsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  def "can return the details of an event"() {
    given:
    def eventId = UUID.randomUUID()
    def kimId = UUID.randomUUID()
    jongo.collection("eventdetails_view") << [
        _id: eventId, name: "cool event",
        participants: [[id: kimId, name: "kim", share: 1, email: null]]
    ]
    def handler = new EventDetailsSearchHandler()

    when:
    def eventDetails = handler.execute(new EventDetailsSearch(eventId), jongo.jongo())

    then:
    eventDetails != null
    eventDetails.id == eventId
    eventDetails.name == "cool event"
    eventDetails.participants[0].name == "kim"
  }
}
