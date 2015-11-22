package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.search.event.activity.search.HistorySearch
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.SearchBus
import spock.lang.Specification

class GetHistoryTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  GetHistory action

  def setup() {
    action = new GetHistory(searchBus)
  }

  def "can return the history of an event"() {
    given:
    def summaries = Mock(List)
    searchBus.sendAndWaitResponse(_ as HistorySearch) >> ExecutionResult.success(summaries)

    when:
    def result = action.get(UUID.randomUUID().toString(), "reminders", 1)

    then:
    result.get() == summaries
  }
}
