package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails
import agoodfriendalwayspayshisdebts.search.event.details.search.EventDetailsSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventDetailsTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventDetails action = new GetEventDetails(searchBus)

  def "can return the details of an event"() {
    given:
    def eventDetails = new EventDetails()
    searchBus.sendAndWaitResponse(_ as EventDetailsSearch) >> ExecutionResult.success(eventDetails)

    when:
    def optionalEvent = action.retrieve(UUID.randomUUID().toString())

    then:
    optionalEvent.get() == eventDetails
  }
}
