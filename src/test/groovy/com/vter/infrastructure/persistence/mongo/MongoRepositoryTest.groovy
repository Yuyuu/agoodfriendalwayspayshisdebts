package com.vter.infrastructure.persistence.mongo

import org.junit.Rule
import spock.lang.Specification

class MongoRepositoryTest extends Specification {

  @Rule
  WithMongoLink mongoLink = WithMongoLink.withPackage("com.vter.infrastructure.persistence.mongo.mapping")

  FakeAggregateRepository repository

  def setup() {
    repository = new FakeAggregateRepository(mongoLink.currentSession())
  }

  def "can retrieve an aggregate"() {
    given:
    mongoLink.collection("fakeaggregate") << [_id: "hello"]

    when:
    def fakeAggregate = repository.get("hello")

    then:
    fakeAggregate != null
  }

  def "can add an aggregate"() {
    when:
    repository.add(new FakeAggregate("hello"))
    mongoLink.cleanSession()

    then:
    def foundAggregate = mongoLink.collection("fakeaggregate").findOne(_id: "hello")
    foundAggregate != null
  }

  def "can delete an aggregate"() {
    given:
    def fakeAggregate = new FakeAggregate("hello")
    repository.add(fakeAggregate)

    when:
    repository.delete(fakeAggregate)
    mongoLink.cleanSession()

    then:
    mongoLink.collection("fakeaggregate").findOne() == null
  }
}
