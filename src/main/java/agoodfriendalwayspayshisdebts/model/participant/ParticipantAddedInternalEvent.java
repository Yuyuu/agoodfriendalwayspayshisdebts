package agoodfriendalwayspayshisdebts.model.participant;

import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.Synchronous;

import java.util.UUID;

@Synchronous
public class ParticipantAddedInternalEvent implements InternalEvent {

  public ParticipantAddedInternalEvent(UUID eventId, Participant participant) {
    this.eventId = eventId;
    this.participant = participant;
  }

  public final UUID eventId;
  public final Participant participant;
}
