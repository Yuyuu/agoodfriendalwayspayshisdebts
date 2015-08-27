package agoodfriendalwayspayshisdebts.search.event.result.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventResultSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID kimId = UUID.randomUUID()
  UUID benId = UUID.randomUUID()
  UUID eventId = UUID.randomUUID()

  EventResultSearchHandler handler = new EventResultSearchHandler()

  def "can return the result of an event"() {
    given:
    jongo.collection("eventresult_view") << [
        _id: eventId,
        participantsResults: [
            (kimId.toString()): [participantName: "kim", totalSpent: 5D, totalDebt: 0D, debtsDetail: [(benId.toString()): [creditorName: "ben", amount: 0D]]],
            (benId.toString()): [participantName: "ben", totalSpent: 0D, totalDebt: 2.5D, debtsDetail: [(kimId.toString()): [creditorName: "ben", amount: 2.5D]]]
        ]
    ]

    when:
    def eventResult = handler.execute(new EventResultSearch(eventId), jongo.jongo())

    then:
    eventResult.eventId == eventId
    eventResult.participantsResults.get(kimId).participantName() == "kim"
    eventResult.participantsResults.get(benId).participantName() == "ben"
    eventResult.participantsResults.get(kimId).totalSpent() == 5D
    eventResult.participantsResults.get(benId).totalSpent() == 0D
    eventResult.participantsResults.get(kimId).totalDebt() == 0D
    eventResult.participantsResults.get(benId).totalDebt() == 2.5D
    eventResult.participantsResults.get(kimId).debtTowards(benId) == 0D
    eventResult.participantsResults.get(benId).debtTowards(kimId) == 2.5D
  }
}
