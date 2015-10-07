package agoodfriendalwayspayshisdebts.search.expense.metadata.search

import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class EventExpensesMetadataSearchHandlerTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  UUID eventId = UUID.randomUUID()

  EventExpensesMetadataSearchHandler handler = new EventExpensesMetadataSearchHandler()

  def "returns the metadata of the expenses"() {
    given:
    def expenseId = UUID.randomUUID()
    jongo.collection("expensesmetadata_view") << [
        _id: eventId, metadata: [[id: expenseId, label: "e1"], [id: UUID.randomUUID(), label: "e2"]]
    ]

    when:
    def metadata = handler.execute(new EventExpensesMetadataSearch(eventId), jongo.jongo())

    then:
    metadata.size() == 2
    def expenseMetadata = metadata.first()
    expenseMetadata.id == expenseId
    expenseMetadata.label == "e1"
  }

  def "returns null if the document does not exist for the event"() {
    expect:
    handler.execute(new EventExpensesMetadataSearch(eventId), jongo.jongo()) == null
  }
}
