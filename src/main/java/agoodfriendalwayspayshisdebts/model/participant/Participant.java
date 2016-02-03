package agoodfriendalwayspayshisdebts.model.participant;

import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import com.vter.model.BaseEntityWithUuid;
import com.vter.model.internal_event.InternalEventBus;

import java.util.UUID;

public class Participant extends BaseEntityWithUuid {

  private String name;
  private int share;
  private String email;
  private UUID eventId;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Participant() {}

  public Participant(String name, int share, String email) {
    this.name = name;
    this.share = share;
    this.email = email;
  }

  public String name() {
    return name;
  }

  public int share() {
    return share;
  }

  public String email() {
    return email;
  }

  public UUID eventId() {
    return eventId;
  }

  public void eventId(UUID eventId) {
    this.eventId = eventId;
  }

  public void update(String email) {
    this.email = email;
    InternalEventBus.INSTANCE().publish(
        new ParticipantUpdatedInternalEvent(eventId, getId()),
        new OperationPerformedInternalEvent(eventId, OperationType.PARTICIPANT_EDITED, name)
    );
  }
}
