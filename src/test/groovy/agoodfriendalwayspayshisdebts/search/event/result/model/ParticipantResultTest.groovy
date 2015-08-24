package agoodfriendalwayspayshisdebts.search.event.result.model

import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class ParticipantResultTest extends Specification {
  def kim = new Participant("kim", 1, null)
  def ben = new Participant("ben", 1, null)

  def "keeps the total debt up to date"() {
    given:
    def kimResult = ParticipantResult.forParticipantId(kim.id(), [ben.id()])

    when:
    kimResult.updateDebtTowards(ben.id(), 3D)

    then:
    kimResult.totalDebt() == 3D
  }

  def "increases the total amount spent"() {
    given:
    def kimResult = ParticipantResult.forParticipantId(kim.id(), [ben.id()])

    when:
    kimResult.increaseTotalAmountSpentBy(3D)
    kimResult.increaseTotalAmountSpentBy(0.5D)

    then:
    kimResult.totalSpent() == 3.5D
  }
}
