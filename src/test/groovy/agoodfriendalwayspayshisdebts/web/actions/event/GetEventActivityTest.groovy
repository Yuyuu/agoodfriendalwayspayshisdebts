package agoodfriendalwayspayshisdebts.web.actions.event

import com.google.common.collect.Lists
import spock.lang.Specification

class GetEventActivityTest extends Specification {
  GetEventActivity action = new GetEventActivity()

  def "responds with fake data"() {
    expect:
    Lists.newArrayList(action.get(UUID.randomUUID().toString()).get()).size() == 5
  }
}
