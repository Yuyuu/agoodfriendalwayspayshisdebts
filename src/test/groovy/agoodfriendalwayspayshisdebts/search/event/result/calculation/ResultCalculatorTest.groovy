package agoodfriendalwayspayshisdebts.search.event.result.calculation

import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Shared
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class ResultCalculatorTest extends Specification {
  @Shared Participant kim = new Participant("kim", 1, null)
  @Shared Participant ben = new Participant("ben", 1, null)

  AmountSpentCalculator amountSpentCalculator = Mock(AmountSpentCalculator)
  DebtsDetailsCalculator debtsDetailsCalculator = Mock(DebtsDetailsCalculator)

  ResultCalculator calculator

  def "returns the result of all the calculations"() {
    given:
    def event = new Event("event", [kim, ben])

    and:
    def kimResult = new ParticipantDebtsDetails(totalDebt: 3D, details: [(ben.id): 3D])
    def benResult = new ParticipantDebtsDetails(totalDebt: 0D, details: [(kim.id): 0D])
    amountSpentCalculator.calculate() >> [(kim.id): 7.5D, (ben.id): 3D]
    debtsDetailsCalculator.calculate() >> [(kim.id): kimResult, (ben.id): benResult]

    when:
    calculator = new ResultCalculator(event)
    calculator.amountSpentCalculator = amountSpentCalculator
    calculator.debtsDetailsCalculator = debtsDetailsCalculator
    def result = calculator.calculate()

    then:
    result.eventId == event.id
    result.participantsResults[kim.id].totalSpent == 7.5D
    result.participantsResults[kim.id].participantDebtsDetails.totalDebt() == 3D
    result.participantsResults[kim.id].participantDebtsDetails.debtTowards(ben.id) == 3D
    result.participantsResults[ben.id].totalSpent == 3D
    result.participantsResults[ben.id].participantDebtsDetails.totalDebt() == 0D
    result.participantsResults[ben.id].participantDebtsDetails.debtTowards(kim.id) == 0D
  }
}
