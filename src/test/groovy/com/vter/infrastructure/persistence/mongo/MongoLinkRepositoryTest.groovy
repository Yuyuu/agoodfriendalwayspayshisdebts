package com.vter.infrastructure.persistence.mongo

import org.junit.Rule
import spock.lang.Specification

class MongoLinkRepositoryTest extends Specification {

  @Rule
  WithMongoLink mongoLink = WithMongoLink.withPackage("com.vter.infrastructure.persistence.mongo.mapping")

  FakeEntityRepository repository

  def setup() {
    repository = new FakeEntityRepository(mongoLink.currentSession())
  }

  def "can retrieve an entity"() {
    given:
    mongoLink.collection("fakeentity") << [_id: "hello"]

    when:
    def fakeEntity = repository.get("hello")

    then:
    fakeEntity != null
  }

  def "can add an entity"() {
    when:
    repository.save(new FakeEntity("hello"))
    mongoLink.cleanSession()

    then:
    def foundEntity = mongoLink.collection("fakeentity").findOne(_id: "hello")
    foundEntity != null
  }

  def "can delete an entity"() {
    given:
    def fakeEntity = new FakeEntity("hello")
    repository.save(fakeEntity)

    when:
    repository.delete(fakeEntity)
    mongoLink.cleanSession()

    then:
    mongoLink.collection("fakeentity").findOne() == null
  }
}
