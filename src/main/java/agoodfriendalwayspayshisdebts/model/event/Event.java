package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.UnknownParticipant;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vter.model.BaseAggregateWithUuid;
import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.InternalEventBus;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Event extends BaseAggregateWithUuid {

  private String name;
  private Set<Participant> participants = Sets.newHashSet();
  private List<Expense> expenses = Lists.newArrayList();
  private List<Operation> operations = Lists.newArrayList();

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Event() {}

  public Event(String name, Collection<Participant> participants) {
    this.name = name;
    participants.forEach(participant -> participant.eventId(this.getId()));
    this.participants.addAll(participants);
  }

  public static Event createAndPublishInternalEvent(String name, Collection<Participant> participants) {
    final Event event = new Event(name, participants);
    publishInternalEvent(new EventCreatedInternalEvent(event.getId()));
    return event;
  }

  public Set<Participant> participants() {
    return participants;
  }

  public List<Expense> expenses() {
    return expenses;
  }

  public String name() {
    return name;
  }

  public List<Operation> operations() {
    return operations;
  }

  public void addExpense(Expense expense) {
    expenses.add(expense);
    publishInternalEvent(new ExpenseAddedInternalEvent(expense));
  }

  public Expense deleteExpense(UUID expenseId) {
    final Expense expense = find(expenseId);
    expenses.remove(expense);
    publishInternalEvent(new ExpenseDeletedInternalEvent(expense));
    return expense;
  }

  public void addParticipant(Participant participant) {
    participant.eventId(getId());
    participants.add(participant);
    publishInternalEvent(new ParticipantAddedInternalEvent(getId(), participant));
  }

  public Participant findParticipant(UUID participantId) {
    return participants.stream()
        .filter(participant -> participantId.equals(participant.getId()))
        .findFirst()
        .orElseThrow(UnknownParticipant::new);
  }

  public void addOperation(Operation operation) {
    operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(getId(), operation.getId()));
  }

  private Expense find(UUID expenseId) {
    return expenses.stream()
        .filter(expense -> expense.getId().equals(expenseId))
        .findFirst()
        .orElseThrow(UnknownExpense::new);
  }

  private static <TInternalEvent extends InternalEvent> void publishInternalEvent(TInternalEvent internalEvent) {
    InternalEventBus.INSTANCE().publish(internalEvent);
  }
}
