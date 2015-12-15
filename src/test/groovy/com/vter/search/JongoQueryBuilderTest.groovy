package com.vter.search

import org.jongo.marshall.jackson.oid.MongoId
import org.junit.Rule
import spock.lang.Specification

class JongoQueryBuilderTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  JongoQueryBuilder builder = JongoQueryBuilder.create("collection")

  def "returns all the elements of the collection when no query is provided"() {
    given:
    jongo.collection("collection") << [[_id: 1, color: "blue"], [_id: 2, color: "red"]]

    when:
    def items = builder.find(jongo.jongo()).as(Item.class).asList()

    then:
    items.size() == 2
  }

  def "can query with a single field"() {
    given:
    jongo.collection("collection") << [[_id: 1, color: "blue"], [_id: 2, color: "red"], [_id: 3, color: "blue"]]

    when:
    def items = builder.add("color", "'blue'").find(jongo.jongo()).as(Item.class).asList()

    then:
    items.size() == 2
  }

  def "can search for the first result"() {
    given:
    jongo.collection("collection") << [[_id: 1, color: "blue"], [_id: 2, color: "red"], [_id: 3, color: "blue"]]

    when:
    def item = builder.add("color", "#", "blue").findOne(jongo.jongo()).as(Item.class)

    then:
    item.id == 1
    item.color == "blue"
  }

  def "can query with multiple fields"() {
    given:
    jongo.collection("collection") << [
        [_id: 1, color: "blue", code: 0], [_id: 2, color: "red", code: 0], [_id: 3, color: "blue", code: 1],
        [_id: 4, color: "green", code: 1]
    ]

    when:
    def items = builder.add("color", "#", "blue").add("code", "#", 1).find(jongo.jongo()).as(Item.class).asList()

    then:
    items.size() == 1
    items[0].id == 3
    items[0].color == "blue"
    items[0].code == 1
  }

  def "can use complex queries"() {
    given:
    jongo.collection("collection") << [
        [_id: 1, color: "blue", code: 0], [_id: 2, color: "red", code: 0], [_id: 3, color: "blue", code: 1],
        [_id: 4, color: "green", code: 0]
    ]

    when:
    def items = builder.add("code", "#", 0).add("color", "{\$in:#}", ["blue", "green"]).find(jongo.jongo()).as(Item.class).asList()

    then:
    items.size() == 2
    items[0].id == 1
    items[1].id == 4
  }

  private static class Item {
    @MongoId
    int id
    String color
    int code
  }
}
