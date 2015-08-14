package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.vter.model.EntityWithUuid;
import com.vter.model.internal_event.InternalEventBus;

import java.util.List;
import java.util.UUID;

public class Event implements EntityWithUuid {

  private UUID id;
  private String name;
  private List<Participant> participants = Lists.newArrayList();
  private List<Expense> expenses = Lists.newArrayList();

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Event() {}

  public Event(String name, List<Participant> participants) {
    id = UUID.randomUUID();
    this.name = name;
    this.participants.addAll(participants);
  }

  public static Event createAndPublishEvent(String name, List<Participant> participants) {
    final Event event = new Event(name, participants);
    InternalEventBus.INSTANCE().publish(new EventCreatedInternalEvent(event.id));
    return event;
  }

  @Override
  public UUID getId() {
    return id;
  }

  public List<Participant> participants() {
    return participants;
  }

  public List<Expense> expenses() {
    return expenses;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("id", id)
        .add("name", name)
        .toString();
  }
}
