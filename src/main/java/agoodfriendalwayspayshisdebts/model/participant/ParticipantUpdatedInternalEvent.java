package agoodfriendalwayspayshisdebts.model.participant;

import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.Synchronous;

import java.util.UUID;

@Synchronous
public class ParticipantUpdatedInternalEvent implements InternalEvent {

  public ParticipantUpdatedInternalEvent(UUID eventId, UUID participantId) {
    this.eventId = eventId;
    this.participantId = participantId;
  }

  public final UUID eventId;
  public final UUID participantId;
}
