package agoodfriendalwayspayshisdebts.search.event.details.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantUpdatedInternalEvent;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnParticipantUpdated implements InternalEventHandler<ParticipantUpdatedInternalEvent> {

  @Inject
  public OnParticipantUpdated(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantUpdatedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final Participant participant = event.findParticipant(internalEvent.participantId);
    jongo.getCollection("eventdetails_view")
        .update("{_id:#,'participants.id':#}", internalEvent.eventId, internalEvent.participantId)
        .with("{$set:{participants.$.email:#}}", participant.email());
  }

  private final Jongo jongo;
}
