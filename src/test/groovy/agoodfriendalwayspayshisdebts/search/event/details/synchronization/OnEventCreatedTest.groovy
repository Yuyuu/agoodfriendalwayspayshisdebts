package agoodfriendalwayspayshisdebts.search.event.details.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.event.EventCreatedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnEventCreatedTest extends Specification {

  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  OnEventCreated handler

  def setup() {
    handler = new OnEventCreated(jongo.jongo())
  }

  def "can create the details of the event"() {
    given:
    def event = new Event("cool event", [new Participant("kim", 1, null)])
    RepositoryLocator.events().save(event)
    def internalEvent = new EventCreatedInternalEvent(event.id)

    when:
    handler.executeEvent(internalEvent)

    then:
    def document = jongo.collection("eventdetails_view").findOne()
    document["_id"] == event.id
    document["name"] == event.name()
    document["participants"][0]["name"] == "kim"
  }
}
