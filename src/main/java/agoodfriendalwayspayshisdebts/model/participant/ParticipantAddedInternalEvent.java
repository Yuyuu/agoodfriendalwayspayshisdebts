package agoodfriendalwayspayshisdebts.model.participant;

import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.Synchronous;

import java.util.List;
import java.util.UUID;

@Synchronous
public class ParticipantAddedInternalEvent implements InternalEvent {

  public ParticipantAddedInternalEvent(UUID eventId, Participant participant, List<UUID> expensesIds) {
    this.eventId = eventId;
    this.participant = participant;
    this.expensesIds = expensesIds;
  }

  public final UUID eventId;
  public final Participant participant;
  public final List<UUID> expensesIds;
}
