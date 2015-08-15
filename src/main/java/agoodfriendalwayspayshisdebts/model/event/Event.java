package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.vter.model.EntityWithUuid;
import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.InternalEventBus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Event implements EntityWithUuid {

  private UUID id;
  private String name;
  private Set<Participant> participants = Sets.newHashSet();
  private Set<Expense> expenses = Sets.newHashSet();

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
    publishInternalEvent(new EventCreatedInternalEvent(event.id));
    return event;
  }

  @Override
  public UUID getId() {
    return id;
  }

  public Set<Participant> participants() {
    return participants;
  }

  public Set<Expense> expenses() {
    return expenses;
  }

  public String name() {
    return name;
  }

  public void addExpense(Expense expense) {
    expenses.add(expense);
    publishInternalEvent(new ExpenseAddedInternalEvent(id, expense));
  }

  private static <TInternalEvent extends InternalEvent> void publishInternalEvent(TInternalEvent internalEvent) {
    InternalEventBus.INSTANCE().publish(internalEvent);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("id", id)
        .add("name", name)
        .toString();
  }
}
