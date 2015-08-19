package agoodfriendalwayspayshisdebts.search.event.result.calculation

import org.junit.Assert
import spock.lang.Specification

class ParticipantDebtsDetailsTest extends Specification {

  def "can increase the debt towards a participant"() {
    given:
    def id = UUID.randomUUID()
    def participantDetails = ParticipantDebtsDetails.initForIds([id])

    when:
    participantDetails.increaseDebtTowards(id, 3)
    participantDetails.increaseDebtTowards(id, 4)

    then:
    participantDetails.debtTowards(id) == 7D
  }

  def "updates the total debt of the participant"() {
    given:
    def id1 = UUID.randomUUID()
    def id2 = UUID.randomUUID()
    def participantDetails = ParticipantDebtsDetails.initForIds([id1, id2])

    when:
    participantDetails.setFinalDebtTowards(id1, 5)
    participantDetails.setFinalDebtTowards(id2, 7.99)

    then:
    Assert.assertEquals(12.99D, participantDetails.totalDebt(), 0.000001)
  }
}
