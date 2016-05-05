package com.vter.search

import spock.lang.Specification

class PaginationTargetTest extends Specification {

  def "has the right relation value"() {
    expect:
    target.toString() == rel

    where:
    target                    || rel
    PaginationTarget.FIRST    || "first"
    PaginationTarget.LAST     || "last"
    PaginationTarget.NEXT     || "next"
    PaginationTarget.PREVIOUS || "prev"
  }
}
