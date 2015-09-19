package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.result.search.EventResultSearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventResultTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventResult action = new GetEventResult(searchBus)

  def "can return the result of an event"() {
    given:
    def eventResult = Mock(Iterable)
    searchBus.sendAndWaitResponse(_ as EventResultSearch) >> ExecutionResult.success(eventResult)

    when:
    def optionalEventResult = action.retrieve(UUID.randomUUID().toString())

    then:
    optionalEventResult.get() == eventResult
  }
}
