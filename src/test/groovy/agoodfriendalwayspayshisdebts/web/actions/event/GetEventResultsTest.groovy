package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.results.search.EventResultsSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventResultsTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventResults action = new GetEventResults(searchBus)

  def "can return the result of an event"() {
    given:
    def eventResult = Mock(Iterable)
    searchBus.sendAndWaitResponse(_ as EventResultsSearch) >> ExecutionResult.success(eventResult)

    when:
    def optionalEventResult = action.retrieve(UUID.randomUUID().toString())

    then:
    optionalEventResult.get() == eventResult
  }
}
