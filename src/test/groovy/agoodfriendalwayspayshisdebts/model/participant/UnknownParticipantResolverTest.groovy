package agoodfriendalwayspayshisdebts.model.participant

import spock.lang.Specification

class UnknownParticipantResolverTest extends Specification {
  UnknownParticipantResolver resolver = new UnknownParticipantResolver()

  def "an unknown participant exception is a 404 error"() {
    expect:
    resolver.status() == 404
  }

  def "has no json representation"() {
    expect:
    resolver.representation(new UnknownParticipant()) == null
  }
}
