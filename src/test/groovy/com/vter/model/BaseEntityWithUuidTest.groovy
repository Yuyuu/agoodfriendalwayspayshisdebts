package com.vter.model

import spock.lang.Specification

class BaseEntityWithUuidTest extends Specification {

  def "generates an id upon construction"() {
    given:
    def fakeEntity = new FakeEntity()

    expect:
    fakeEntity.id != null
  }

  class FakeEntity extends BaseEntityWithUuid {
  }
}
