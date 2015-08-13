package agoodfriendalwayspayshisdebts.web.action

import net.codestory.http.errors.NotFoundException
import spock.lang.Specification

class UuidParserTest extends Specification {

  def "creates an uuid based on its string representation"() {
    given:
    def uuid = UUID.randomUUID()

    expect:
    UuidParser.createOrThrowNotFoundExceptionOnInvalidUuid(uuid.toString()) == uuid
  }

  def "throws a not found exception in case of invalid uuid"() {
    when:
    UuidParser.createOrThrowNotFoundExceptionOnInvalidUuid("hello")

    then:
    thrown(NotFoundException)
  }
}
