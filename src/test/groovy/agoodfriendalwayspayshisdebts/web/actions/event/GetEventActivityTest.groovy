package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.activity.search.EventActivitySearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetEventActivityTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetEventActivity action

  def setup() {
    action = new GetEventActivity(searchBus)
  }

  def "can return the operations of an event"() {
    given:
    def operations = Mock(List)
    searchBus.sendAndWaitResponse(_ as EventActivitySearch) >> ExecutionResult.success(operations)

    when:
    def result = action.get(UUID.randomUUID().toString(), 1)

    then:
    result.get() == operations
  }
}
