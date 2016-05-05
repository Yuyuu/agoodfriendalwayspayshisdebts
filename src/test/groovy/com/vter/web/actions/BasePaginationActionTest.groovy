package com.vter.web.actions

import com.vter.infrastructure.bus.ExecutionResult
import com.vter.search.PaginatedSearchResult
import com.vter.search.Search
import com.vter.search.SearchBus
import net.codestory.http.payload.Payload
import spock.lang.Specification

class BasePaginationActionTest extends Specification {
  SearchBus searchBus = Mock(SearchBus)

  def "builds a payload of the search result with the pagination links in the Link header"() {
    given:
    def result = new FakeResult(totalCount: 3, items: ["world"])
    searchBus.sendAndWaitResponse(_ as FakeSearch) >> ExecutionResult.success(result)

    when:
    def payload = new FakeAction(searchBus).get(1, 2)

    then:
    def payloadContent = payload.rawContent() as FakeResult
    payloadContent.totalCount == 3
    payloadContent.items == ["world"]
    payload.headers().get("Link") == "</fake?page=3>; rel=\"next\", </fake?page=3>; rel=\"last\", </fake?page=1>; rel=\"first\", </fake?page=1>; rel=\"prev\""
  }

  def "does not add the Link header if no pagination link is available"() {
    given:
    def result = new FakeResult(totalCount: 1, items: ["world"])
    searchBus.sendAndWaitResponse(_ as FakeSearch) >> ExecutionResult.success(result)

    when:
    def payload = new FakeAction(searchBus).get(2, 1)

    then:
    def payloadContent = payload.rawContent() as FakeResult
    payloadContent.totalCount == 1
    payloadContent.items == ["world"]
    payload.headers().get("Link") == null
  }

  def "still returns a payload when no result is found"() {
    given:
    searchBus.sendAndWaitResponse(_ as FakeSearch) >> ExecutionResult.success(null)

    when:
    def payload = new FakeAction(searchBus).get(1, 2)

    then:
    payload.rawContent() == null
    payload.headers().get("Link") == null
  }

  private static class FakeResult extends PaginatedSearchResult<String> {}

  private static class FakeSearch extends Search<FakeResult> {}

  private static class FakeAction extends BasePaginationAction {
    FakeAction(SearchBus searchBus) {
      super(searchBus)
    }

    Payload get(int perPage, int page) {
      return paginationPayload(new FakeSearch().perPage(perPage).page(page))
    }

    protected <TSearch extends Search<?>> String resourcePartialUri(TSearch tSearch) {
      return "/fake?"
    }
  }
}
