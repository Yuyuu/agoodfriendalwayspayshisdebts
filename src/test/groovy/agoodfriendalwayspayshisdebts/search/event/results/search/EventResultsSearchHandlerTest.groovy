package agoodfriendalwayspayshisdebts.search.event.results.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventResultsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID kimId = UUID.randomUUID()
  UUID benId = UUID.randomUUID()
  UUID eventId = UUID.randomUUID()

  EventResultsSearchHandler handler = new EventResultsSearchHandler()

  def "can return the result of an event"() {
    given:
    jongo.collection("eventresults_view") << [
        _id: eventId,
        participantsResults: [
            (kimId.toString()): [participantName: "kim", totalSpent: 5D, totalDebt: 0D, debtsDetails: [(benId.toString()): [creditorName: "ben", rawAmount: 0D, mitigatedAmount: 0D]]],
            (benId.toString()): [participantName: "ben", totalSpent: 0D, totalDebt: 2.5D, debtsDetails: [(kimId.toString()): [creditorName: "ben", rawAmount: 2.5D, mitigatedAmount: 2.5D]]]
        ]
    ]

    when:
    def results = handler.execute(new EventResultsSearch(eventId), jongo.jongo())

    then:
    results.size() == 2
    results[0].participantName() == "kim"
    results[1].participantName() == "ben"
    results[0].totalSpent() == 5D
    results[1].totalSpent() == 0D
    results[0].totalDebt() == 0D
    results[1].totalDebt() == 2.5D
    results[0].mitigatedDebtTowards(benId) == 0D
    results[1].mitigatedDebtTowards(kimId) == 2.5D
    results[0].rawDebtTowards(benId) == 0D
    results[1].rawDebtTowards(kimId) == 2.5D
  }
}
