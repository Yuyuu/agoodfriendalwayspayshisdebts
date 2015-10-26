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
    def expenseId = UUID.randomUUID()
    mongoLink.collection("event") << [
        _id: id, name: "event",
        participants: [[id: kimId, name: "kim", share: 1, email: "kim@m.com", eventId: id]],
        expenses: [[id: expenseId, label: "errands", purchaserId: kimId, amount: 10, participantsIds: [kimId], description: "hello", eventId: id]]
    ]

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
    kim.eventId() == id
    def expense = event.expenses().first()
    expense.id() == expenseId
    expense.label() == "errands"
    expense.purchaserId() == kimId
    expense.amount() == 10
    expense.participantsIds() == [kimId] as Set
    expense.description() == "hello"
    expense.eventId() == id
  }

  def "can add an event"() {
    when:
    def event = new Event("event", [new Participant("kim", 1, null)])
    repository.save(event)
    mongoLink.cleanSession()

    then:
    def foundEvent = mongoLink.collection("event").findOne(_id: event.id)
    foundEvent["_id"] == event.id
    foundEvent["name"] == "event"
    def kim = foundEvent["participants"][0]
    kim["name"] == "kim"
    kim["share"] == 1
    kim["eventId"] == event.id
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
