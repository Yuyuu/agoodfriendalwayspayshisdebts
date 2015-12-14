package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo

import agoodfriendalwayspayshisdebts.model.activity.Operation
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import com.vter.infrastructure.persistence.mongo.WithMongoLink
import org.joda.time.DateTime
import org.junit.Rule
import spock.lang.Specification

class MongoOperationRepositoryTest extends Specification {
  @Rule
  WithMongoLink mongoLink = WithMongoLink.withPackage("agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping")

  MongoOperationRepository repository

  def setup() {
    repository = new MongoOperationRepository(mongoLink.currentSession())
  }

  def "can retrieve an operation"() {
    given:
    def id = UUID.randomUUID()
    def creationDate = DateTime.now()
    def eventId = UUID.randomUUID()
    mongoLink.collection("operation") << [
        _id: id, type: "EVENT_CREATION", creationDate: creationDate.millis, data: "hello", eventId: eventId
    ]

    when:
    def operation = repository.get(id)

    then:
    operation.id == id
    operation.type() == OperationType.EVENT_CREATION
    operation.creationDate() == creationDate
    operation.data() == "hello"
    operation.eventId() == eventId
  }

  def "can add an operation"() {
    given:
    def eventId = UUID.randomUUID()
    def operation = new Operation(OperationType.EVENT_CREATION, "hello", eventId)

    when:
    repository.add(operation)
    mongoLink.cleanSession()

    then:
    def foundOperation = mongoLink.collection("operation").findOne(_id: operation.id)
    foundOperation["_id"] == operation.id
    foundOperation["type"] == OperationType.EVENT_CREATION.toString()
    foundOperation["creationDate"] == operation.creationDate().millis
    foundOperation["data"] == "hello"
    foundOperation["eventId"] == eventId
  }

  def "can delete an operation"() {
    given:
    def operation = new Operation(OperationType.EVENT_CREATION, "hello", UUID.randomUUID())
    repository.add(operation)

    when:
    repository.delete(operation)
    mongoLink.cleanSession()

    then:
    mongoLink.collection("operation").findOne() == null
  }
}
