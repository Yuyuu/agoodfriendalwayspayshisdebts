package com.vter.model

import spock.lang.Specification

class BaseEntityTest extends Specification {

  def "two entities with the same id are equal"() {
    expect:
    new FakeEntity("1") == new FakeEntity("1")
  }

  def "two entities with different ids are not equal"() {
    expect:
    new FakeEntity("1") != new FakeEntity("2")
  }

  class FakeEntity extends BaseEntity<String> {

    FakeEntity(String id) {
      super(id)
    }
  }
}
