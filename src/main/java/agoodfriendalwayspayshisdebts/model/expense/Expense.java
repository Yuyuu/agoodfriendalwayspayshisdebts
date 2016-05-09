package agoodfriendalwayspayshisdebts.model.expense;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent;
import com.google.common.collect.Sets;
import com.vter.model.BaseEntityWithUuid;
import com.vter.model.internal_event.InternalEventBus;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Expense extends BaseEntityWithUuid {

  private String label;
  private State state = State.PENDING;
  private UUID purchaserId;
  private double amount;
  private Set<UUID> participantsIds = Sets.newHashSet();
  private String description;
  private DateTime creationDate = DateTime.now();
  private UUID eventId;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Expense() {}

  public Expense(String label, UUID purchaserId, double amount, List<UUID> participantsIds, UUID eventId) {
    this.label = label;
    this.purchaserId = purchaserId;
    this.amount = amount;
    this.participantsIds.addAll(participantsIds);
    this.eventId = eventId;
  }

  public double amount() {
    return amount;
  }

  public String description() {
    return description;
  }

  public UUID eventId() {
    return eventId;
  }

  public String label() {
    return label;
  }

  public Set<UUID> participantsIds() {
    return participantsIds;
  }

  public UUID purchaserId() {
    return purchaserId;
  }

  public State state() {
    return state;
  }

  public DateTime creationDate() {
    return creationDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void state(State state) {
    this.state = state;
  }

  public void includeParticipant(Participant participant) {
    participantsIds.add(participant.getId());
    InternalEventBus.INSTANCE().publish(new ParticipantIncludedInternalEvent(this, participant));
  }
}
