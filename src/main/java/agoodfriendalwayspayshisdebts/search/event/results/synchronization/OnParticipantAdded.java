package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.operation.AddParticipantOperation;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OnParticipantAdded implements InternalEventHandler<ParticipantAddedInternalEvent> {

  @Inject
  public OnParticipantAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final EventResults result = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", internalEvent.eventId)
        .as(EventResults.class);
    assert result != null;

    result.apply(new AddParticipantOperation(internalEvent.participant, participantsNames(event)));

    jongo.getCollection("eventresults_view").update("{_id:#}", internalEvent.eventId).with(result);
  }

  private static Map<UUID, String> participantsNames(Event event) {
    return event.participants().stream().collect(Collectors.toMap(Participant::id, Participant::name));
  }

  private final Jongo jongo;
}
