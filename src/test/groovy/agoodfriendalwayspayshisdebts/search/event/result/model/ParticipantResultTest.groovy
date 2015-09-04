package agoodfriendalwayspayshisdebts.search.event.result.model

import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class ParticipantResultTest extends Specification {
  def kim = new Participant("kim", 1, null)
  def ben = new Participant("ben", 1, null)

  def "keeps the total debt up to date"() {
    given:
    def kimResult = ParticipantResult.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.updateDebtTowards(ben.id(), 3D)

    then:
    kimResult.totalDebt() == 3D
  }

  def "increases the total amount spent"() {
    given:
    def kimResult = ParticipantResult.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseTotalAmountSpentBy(3D)
    kimResult.increaseTotalAmountSpentBy(0.5D)

    then:
    kimResult.totalSpent() == 3.5D
  }

  def "decreases the total amount spent"() {
    given:
    def kimResult = ParticipantResult.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseTotalAmountSpentBy(3D)
    kimResult.decreaseTotalAmountSpentBy(1.3D)

    then:
    kimResult.totalSpent() == 1.7D
  }

  def "increases the raw debt towards a participant"() {
    given:
    def kimResult = ParticipantResult.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseRawDebtTowards(ben.id(), 3D)

    then:
    kimResult.rawDebtTowards(ben.id()) == 3D
  }

  def "decreases the raw debt towards a participant"() {
    given:
    def kimResult = ParticipantResult.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseRawDebtTowards(ben.id(), 3D)
    kimResult.decreaseRawDebtTowards(ben.id(), 1D)

    then:
    kimResult.rawDebtTowards(ben.id()) == 2D
  }
}
