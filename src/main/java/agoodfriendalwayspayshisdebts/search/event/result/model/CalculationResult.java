package agoodfriendalwayspayshisdebts.search.event.result.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.operation.ResultOperation;
import com.google.common.collect.Maps;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CalculationResult {
  @MongoId
  public UUID eventId;
  public Map<UUID, ParticipantResult> participantsResults = Maps.newHashMap();

  @SuppressWarnings("unused")
  private CalculationResult() {}

  private CalculationResult(UUID eventId) {
    this.eventId = eventId;
  }

  public static CalculationResult forEvent(Event event) {
    final CalculationResult result = new CalculationResult(event.getId());
    final Map<UUID, String> participantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::id, Participant::name));

    event.participants().forEach(participant -> {
      final ParticipantResult participantResult = ParticipantResult.forParticipant(participant, participantsNames);
      result.participantsResults.put(participant.id(), participantResult);
    });

    return result;
  }

  public void apply(ResultOperation operation) {
    operation.accept(this);
  }
}
