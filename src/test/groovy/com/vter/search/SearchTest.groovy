package com.vter.search

import spock.lang.Specification

class SearchTest extends Specification {

  def "computes the skip value based on the items per page and the page"() {
    given:
    def search = new FakeSearch().perPage(5).page(3)

    expect:
    search.skip() == 10
  }

  private static class FakeSearch extends Search<String> {
  }
}
