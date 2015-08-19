package agoodfriendalwayspayshisdebts.search.event.result.calculation

import spock.lang.Specification

class ParticipantIdPairTest extends Specification {

  def "computes the pair combinations"() {
    given:
    def uuid1 = UUID.randomUUID()
    def uuid2 = UUID.randomUUID()
    def uuid3 = UUID.randomUUID()

    expect:
    ParticipantIdPair.calculateAllFromList([uuid1, uuid2, uuid3]) == [
        pair(uuid1, uuid2),
        pair(uuid1, uuid3),
        pair(uuid2, uuid3)
    ]
  }

  ParticipantIdPair pair(UUID uuid1, UUID uuid2) {
    return new ParticipantIdPair(uuid1, uuid2)
  }
}
