package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo

import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.infrastructure.persistence.mongo.WithMongoLink
import org.junit.Rule
import spock.lang.Specification

class MongoLinkEventRepositoryTest extends Specification {

  @Rule
  WithMongoLink mongoLink = WithMongoLink.withPackage("agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping")

  MongoLinkEventRepository repository

  def setup() {
    repository = new MongoLinkEventRepository(mongoLink.currentSession())
  }

  def "can retrieve an event"() {
    given:
    def id = UUID.randomUUID()
    def kimId = UUID.randomUUID()
    mongoLink.collection("event") << [_id: id, name: "event", participants: [[id: kimId, name: "kim", share: 1, email: "kim@m.com"]]]

    when:
    def event = repository.get(id)

    then:
    event.id == id
    event.name() == "event"
    def kim = event.participants().first()
    kim.id() == kimId
    kim.name() == "kim"
    kim.share() == 1
    kim.email() == "kim@m.com"
  }

  def "can add an event"() {
    when:
    def event = new Event("event", [new Participant("kim", 1, null)])
    repository.save(event)
    mongoLink.cleanSession()

    then:
    def foundEvent = mongoLink.collection("event").findOne(_id: event.id)
    foundEvent != null
  }

  def "can delete an event"() {
    given:
    def event = new Event("event", [new Participant("kim", 1, null)])
    repository.save(event)

    when:
    repository.delete(event)
    mongoLink.cleanSession()

    then:
    mongoLink.collection("event").findOne() == null
  }
}
