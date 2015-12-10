package com.vter.infrastructure.bus

import spock.lang.Specification

class MessageTest extends Specification {

  def "a message requires a new mongo session by default"() {
    expect:
    new Message() {}.requiresNewMongoSession()
  }
}
