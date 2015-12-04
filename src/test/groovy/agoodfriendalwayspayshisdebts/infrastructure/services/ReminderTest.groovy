package agoodfriendalwayspayshisdebts.infrastructure.services

import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults
import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings("GroovyAccessibility")
class ReminderTest extends Specification {
  Participant kim = new Participant("kim", 1, "kim@email.com")
  UUID leaId = UUID.randomUUID()
  ParticipantResults result = ParticipantResults.forParticipant(kim, [(leaId): "lea"])

  def setup() {
    result.totalSpent = 5D
    result.totalDebt = 5.4D
    result.details[leaId].mitigatedDebt = 3D
  }

  @Unroll
  def "generates the email using the template for a given locale"() {
    given:
    def reminder = reminder(result, locale).withEventModel("cool event", "http://link.to.event.fr")

    expect:
    expectedContent == reminder.content()

    where:
    locale << [Locale.FRANCE, null]
    expectedContent << [
        contentFrom("templates/expected_debt_reminder_fr.html"),
        contentFrom("templates/expected_debt_reminder_en.html")
    ]
  }

  @Unroll
  def "adapts the content when the recipient has no debt"() {
    given:
    result.totalDebt = 0
    def reminder = Reminder.forLocale(locale, result).withEventModel("cool event", "http://link.to.event.fr")

    expect:
    expectedContent == reminder.content()

    where:
    locale << [Locale.FRANCE, Locale.UK]
    expectedContent << [
        contentFrom("templates/expected_debt_reminder_no_debt_fr.html"),
        contentFrom("templates/expected_debt_reminder_no_debt_en.html")
    ]
  }

  def "sets the name of the event as the subject of the reminder"() {
    given:
    def reminder = Reminder.withDefaultLocale(result).withEventModel("cool event", "http://link")

    expect:
    reminder.subject() == "cool event"
  }

  def "sets the recipient of the email"() {
    given:
    def reminder = Reminder.withDefaultLocale(result).to("joe+bob@somewhere.com")

    expect:
    reminder.to() == "joe+bob@somewhere.com"
  }

  private static Reminder reminder(ParticipantResults result, Locale locale) {
    return (locale == null) ? Reminder.withDefaultLocale(result) : Reminder.forLocale(locale, result)
  }

  private String contentFrom(String path) {
    return getClass().classLoader.getResource(path).text
  }
}
