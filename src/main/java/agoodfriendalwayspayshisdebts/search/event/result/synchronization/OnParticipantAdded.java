package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.operation.AddParticipantOperation;
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
    final CalculationResult result = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.eventId)
        .as(CalculationResult.class);
    assert result != null;

    result.apply(new AddParticipantOperation(internalEvent.participant, participantsNames(event)));

    jongo.getCollection("eventresult_view").update("{_id:#}", internalEvent.eventId).with(result);
  }

  private static Map<UUID, String> participantsNames(Event event) {
    return event.participants().stream().collect(Collectors.toMap(Participant::id, Participant::name));
  }

  private final Jongo jongo;
}
