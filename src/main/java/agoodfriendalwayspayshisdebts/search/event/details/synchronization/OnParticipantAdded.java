package agoodfriendalwayspayshisdebts.search.event.details.synchronization;

import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.details.model.ParticipantDetails;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnParticipantAdded implements InternalEventHandler<ParticipantAddedInternalEvent> {

  @Inject
  public OnParticipantAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantAddedInternalEvent internalEvent) {
    final ParticipantDetails participantDetails = ParticipantDetails.fromParticipant(internalEvent.participant);
    jongo.getCollection("eventdetails_view")
        .update("{_id:#}", internalEvent.eventId)
        .with("{$push:{participants:#}}", participantDetails);
  }

  private final Jongo jongo;
}
