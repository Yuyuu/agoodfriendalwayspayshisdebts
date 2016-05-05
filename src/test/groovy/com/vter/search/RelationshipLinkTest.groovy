package com.vter.search

import spock.lang.Specification

class RelationshipLinkTest extends Specification {

  def "builds a relationship link for 'first' target"() {
    given:
    def link = RelationshipLink.first(1)

    expect:
    link.page() == 1
    link.target() == PaginationTarget.FIRST
  }

  def "builds a relationship link for 'next' target"() {
    given:
    def link = RelationshipLink.next(3)

    expect:
    link.page() == 3
    link.target() == PaginationTarget.NEXT
  }

  def "builds a relationship link for 'prev' target"() {
    given:
    def link = RelationshipLink.prev(2)

    expect:
    link.page() == 2
    link.target() == PaginationTarget.PREVIOUS
  }

  def "builds a relationship link for 'last' target"() {
    given:
    def link = RelationshipLink.last(5)

    expect:
    link.page() == 5
    link.target() == PaginationTarget.LAST
  }
}
