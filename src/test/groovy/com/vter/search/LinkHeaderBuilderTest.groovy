package com.vter.search

import spock.lang.Specification

class LinkHeaderBuilderTest extends Specification {

  def "does not add 'first' and 'prev' relationships for first page"() {
    given:
    def search = new FakeSearch().perPage(2).page(1)

    when:
    def links = new LinkHeaderBuilder(search, 6).get()

    then:
    links.size() == 2
    links.contains(RelationshipLink.next(2))
    links.contains(RelationshipLink.last(3))
    links.findIndexOf { it.target() == PaginationTarget.FIRST } == -1
    links.findIndexOf { it.target() == PaginationTarget.PREVIOUS } == -1
  }

  def "does not add 'last' and 'next' relationships for last page"() {
    given:
    def search = new FakeSearch().perPage(2).page(3)

    when:
    def links = new LinkHeaderBuilder(search, 6).get()

    then:
    links.size() == 2
    links.contains(RelationshipLink.first(1))
    links.contains(RelationshipLink.prev(2))
    links.findIndexOf { it.target() == PaginationTarget.NEXT } == -1
    links.findIndexOf { it.target() == PaginationTarget.LAST } == -1
  }

  def "can return no links"() {
    given:
    def search = new FakeSearch().perPage(2).page(1)

    when:
    def links = new LinkHeaderBuilder(search, 2).get()

    then:
    links.empty
  }

  def "can return all links"() {
    given:
    def search = new FakeSearch().perPage(2).page(2)

    when:
    def links = new LinkHeaderBuilder(search, 6).get()

    then:
    links.size() == 4
    links.contains(RelationshipLink.first(1))
    links.contains(RelationshipLink.prev(1))
    links.contains(RelationshipLink.next(3))
    links.contains(RelationshipLink.last(3))
  }

  private static class FakeSearch extends Search<String> {}
}
