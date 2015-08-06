package agoodfriendalwayspayshisdebts.model.participant

import spock.lang.Specification

class ParticipantTest extends Specification {

  def "can create a participant with a name and a share"() {
    given:
    def participant = new Participant("kim", 1, "kim@email.com")

    expect:
    participant.id != null
    participant.name() == "kim"
    participant.share() == 1
    participant.email() == "kim@email.com"
  }

  def "two participants with the same id are equal"() {
    given:
    def kim = new Participant("kim", 1, null)
    def ben = new Participant("ben", 1, null)
    ben.id = kim.id

    expect:
    kim == ben
  }

  def "two participants with the same name are not necessarily equal"() {
    given:
    def kim = new Participant("kim", 1, null)
    def kim2 = new Participant("kim", 1, null)

    expect:
    kim != kim2
  }
}
