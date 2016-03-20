package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.operation.AddParticipantOperation;
import agoodfriendalwayspayshisdebts.search.event.results.operation.IncludeParticipantOperation;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OnParticipantIncluded implements InternalEventHandler<ParticipantIncludedInternalEvent> {

  @Inject
  public OnParticipantIncluded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantIncludedInternalEvent internalEvent) {
    final Participant participant = internalEvent.participant;
    final Event event = RepositoryLocator.events().get(participant.eventId());
    final EventResults result = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(EventResults.class);
    assert result != null;

    if (participantResultsDoesNotExist(result, participant.getId())) {
      result.apply(new AddParticipantOperation(participant, participantsNames(event)));
    }
    result.apply(new IncludeParticipantOperation(internalEvent.expense, participant));

    jongo.getCollection("eventresults_view").update("{_id:#}", result.eventId).with(result);
  }

  private static boolean participantResultsDoesNotExist(EventResults result, UUID participantId) {
    return result.participantsResults.get(participantId) == null;
  }

  private static Map<UUID, String> participantsNames(Event event) {
    return event.participants().stream().collect(Collectors.toMap(Participant::getId, Participant::name));
  }

  private final Jongo jongo;
}
