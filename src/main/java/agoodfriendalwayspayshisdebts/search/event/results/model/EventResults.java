package agoodfriendalwayspayshisdebts.search.event.results.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.results.operation.ResultsOperation;
import com.google.common.collect.Maps;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventResults {
  @MongoId
  public UUID eventId;
  public Map<UUID, ParticipantResults> participantsResults = Maps.newHashMap();

  @SuppressWarnings("unused")
  private EventResults() {}

  private EventResults(UUID eventId) {
    this.eventId = eventId;
  }

  public static EventResults forEvent(Event event) {
    final EventResults result = new EventResults(event.getId());
    final Map<UUID, String> participantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::id, Participant::name));

    event.participants().forEach(participant -> {
      final ParticipantResults participantResults = ParticipantResults.forParticipant(participant, participantsNames);
      result.participantsResults.put(participant.id(), participantResults);
    });

    return result;
  }

  public void apply(ResultsOperation operation) {
    operation.accept(this);
  }
}
