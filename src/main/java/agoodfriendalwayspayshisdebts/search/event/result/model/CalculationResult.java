package agoodfriendalwayspayshisdebts.search.event.result.model;

import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Map;
import java.util.UUID;

public class CalculationResult {
  @MongoId
  public UUID eventId;
  public Map<UUID, ParticipantResult> participantsResults;

  public CalculationResult(UUID eventId, Map<UUID, ParticipantResult> participantsResults) {
    this.eventId = eventId;
    this.participantsResults = participantsResults;
  }
}
