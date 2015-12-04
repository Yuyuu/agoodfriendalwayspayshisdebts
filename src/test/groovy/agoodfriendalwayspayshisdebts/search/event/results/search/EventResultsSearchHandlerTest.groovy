package agoodfriendalwayspayshisdebts.search.event.results.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventResultsSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID kimId = UUID.randomUUID()
  String strKimId = kimId.toString()
  UUID benId = UUID.randomUUID()
  String strBenId = benId.toString()
  UUID eventId = UUID.randomUUID()

  EventResultsSearchHandler handler = new EventResultsSearchHandler()

  def "can return the result of an event"() {
    given:
    jongo.collection("eventresults_view") << [
        _id: eventId,
        participantsResults: [
            (strKimId): [participantName: "kim", participantShare: 1, totalSpent: 5D, totalDebt: 0D, totalAdvance: 2.5D, details: [(strBenId): [participantName: "ben", rawDebt: 0D, mitigatedDebt: 0D, advance: 2.5D]]],
            (strBenId): [participantName: "ben", participantShare: 1, totalSpent: 0D, totalDebt: 2.5D, totalAdvance: 0D, details: [(strKimId): [participantName: "kim", rawDebt: 2.5D, mitigatedDebt: 2.5D, advance: 0D]]]
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
    results[0].totalAdvance() == 2.5D
    results[1].totalAdvance() == 0D
    results[0].mitigatedDebtTowards(benId) == 0D
    results[1].mitigatedDebtTowards(kimId) == 2.5D
    results[0].rawDebtTowards(benId) == 0D
    results[1].rawDebtTowards(kimId) == 2.5D
    results[0].advanceTowards(benId) == 2.5D
    results[1].advanceTowards(kimId) == 0D
  }
}
