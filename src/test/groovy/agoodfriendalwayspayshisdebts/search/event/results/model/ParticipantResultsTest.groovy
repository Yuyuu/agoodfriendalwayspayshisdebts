package agoodfriendalwayspayshisdebts.search.event.results.model

import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class ParticipantResultsTest extends Specification {
  def kim = new Participant("kim", 1, null)
  def ben = new Participant("ben", 1, null)

  def "keeps the results up to date when updating the debt towards a participant"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])
    kimResult.debtsDetails().get(ben.id()).mitigatedDebt = 0D
    kimResult.debtsDetails().get(ben.id()).advance = 1D
    kimResult.totalAdvance = 2D

    when:
    kimResult.updateDebtTowards(ben.id(), 3D)

    then:
    kimResult.debtsDetails().get(ben.id()).mitigatedDebt == 3D
    kimResult.debtsDetails().get(ben.id()).advance == 0D
    kimResult.totalDebt() == 3D
    kimResult.totalAdvance() == 1D
  }

  def "keeps the results up to date when updating the advance towards a participant"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])
    kimResult.debtsDetails().get(ben.id()).mitigatedDebt = 5D
    kimResult.debtsDetails().get(ben.id()).advance = 0D
    kimResult.totalDebt = 7D

    when:
    kimResult.updateAdvanceTowards(ben.id(), 3D)

    then:
    kimResult.debtsDetails().get(ben.id()).mitigatedDebt == 0D
    kimResult.debtsDetails().get(ben.id()).advance == 3D
    kimResult.totalDebt() == 2D
    kimResult.totalAdvance() == 3D
  }

  def "increases the total amount spent"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseTotalAmountSpentBy(3D)
    kimResult.increaseTotalAmountSpentBy(0.5D)

    then:
    kimResult.totalSpent() == 3.5D
  }

  def "decreases the total amount spent"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseTotalAmountSpentBy(3D)
    kimResult.decreaseTotalAmountSpentBy(1.3D)

    then:
    kimResult.totalSpent() == 1.7D
  }

  def "increases the raw debt towards a participant"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseRawDebtTowards(ben.id(), 3D)

    then:
    kimResult.rawDebtTowards(ben.id()) == 3D
  }

  def "decreases the raw debt towards a participant"() {
    given:
    def kimResult = ParticipantResults.forParticipant(kim, [(ben.id()): ben.name()])

    when:
    kimResult.increaseRawDebtTowards(ben.id(), 3D)
    kimResult.decreaseRawDebtTowards(ben.id(), 1D)

    then:
    kimResult.rawDebtTowards(ben.id()) == 2D
  }
}
