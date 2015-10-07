package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.model.DebtTowardsParticipant;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OnParticipantAdded implements InternalEventHandler<ParticipantAddedInternalEvent> {

  @Inject
  public OnParticipantAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final CalculationResult calculationResult = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.eventId)
        .as(CalculationResult.class);
    assert calculationResult != null;

    calculationResult.participantsResults.values().forEach(includeParticipant(internalEvent.participant));

    final ParticipantResult participantResult = resultFor(internalEvent.participant, event);
    calculationResult.participantsResults.put(internalEvent.participant.id(), participantResult);

    jongo.getCollection("eventresult_view").update("{_id:#}", internalEvent.eventId).with(calculationResult);
  }

  private static Consumer<ParticipantResult> includeParticipant(Participant participant) {
    return participantResult ->
        participantResult.debtsDetail().put(participant.id(),new DebtTowardsParticipant(participant.name()));
  }

  private static ParticipantResult resultFor(Participant participant, Event event) {
    final Map<UUID, String> participantsNames = participantsNames(event);
    return ParticipantResult.forParticipant(participant, participantsNames);
  }

  private static Map<UUID, String> participantsNames(Event event) {
    return event.participants().stream().collect(Collectors.toMap(Participant::id, Participant::name));
  }

  private final Jongo jongo;
}
