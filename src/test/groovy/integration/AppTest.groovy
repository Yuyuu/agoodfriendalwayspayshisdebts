package integration

class AppTest extends AbstractIntegrationSpecification {

  def "the synchronous application flow should roll"() {
    given:
    def requestBody = """{"name": "cool event", "currency": "€", "participants": [{"name": "kim", "share": 1, "email": ""}]}"""

    when:
    def response = post("/events", requestBody).response().content()
    def eventId = slurper.parseText(response)["id"]
    def eventDetails = slurper.parseText(get("/events/$eventId/meta").response().content())

    then:
    eventDetails["id"] == eventId
    eventDetails["name"] == "cool event"
    eventDetails["currency"] == "€"
    eventDetails["participants"][0]["name"] == "kim"
  }
}
