package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.activity.model.ActivityFilter
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
    searchBus.sendAndWaitResponse({it.filter == ActivityFilter.ALL}) >> ExecutionResult.success(operations)

    when:
    def result = action.get(UUID.randomUUID().toString(), "all", 1)

    then:
    result.get() == operations
  }
}
