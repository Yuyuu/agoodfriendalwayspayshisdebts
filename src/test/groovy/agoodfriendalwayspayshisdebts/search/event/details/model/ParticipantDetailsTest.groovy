package agoodfriendalwayspayshisdebts.search.event.details.model

import agoodfriendalwayspayshisdebts.model.participant.Participant
import spock.lang.Specification

class ParticipantDetailsTest extends Specification {

  def "can create from a participant"() {
    given:
    def lea = new Participant("lea", 1, "lea@m.com")

    when:
    def leaDetails = ParticipantDetails.fromParticipant(lea)

    then:
    leaDetails.id == lea.id()
    leaDetails.name == "lea"
    leaDetails.share == 1
    leaDetails.email == "lea@m.com"
  }
}
