package com.vter.web.fluent.status

import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class ErrorRepresentationTest extends Specification {

  def "can create an error representation from a list of messages"() {
    when:
    def representation = ErrorRepresentation.fromErrorMessages(["hello", "hi"])

    then:
    representation.errors == [[message: "hello"], [message: "hi"]]
  }
}
