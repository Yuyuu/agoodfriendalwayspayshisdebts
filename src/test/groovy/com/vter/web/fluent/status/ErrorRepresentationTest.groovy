package com.vter.web.fluent.status

import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class ErrorRepresentationTest extends Specification {

  def "can create an error representation for a list of messages"() {
    when:
    def representation = ErrorRepresentation.forErrorMessages(["hello", "hi"])

    then:
    representation.errors == [[message: "hello"], [message: "hi"]]
  }
}
