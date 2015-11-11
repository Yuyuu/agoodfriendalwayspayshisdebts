package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.UnknownParticipant;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vter.model.EntityWithUuid;
import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.InternalEventBus;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Event implements EntityWithUuid {

  private UUID id;
  private String name;
  private Set<Participant> participants = Sets.newHashSet();
  private List<Expense> expenses = Lists.newArrayList();
  private List<Operation> operations = Lists.newArrayList();

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Event() {}

  public Event(String name, Collection<Participant> participants) {
    this.id = UUID.randomUUID();
    this.name = name;
    participants.forEach(participant -> participant.eventId(this.id));
    this.participants.addAll(participants);
  }

  public static Event createAndPublishInternalEvent(String name, Collection<Participant> participants) {
    final Event event = new Event(name, participants);
    publishInternalEvent(new EventCreatedInternalEvent(event.id));
    final Operation operation = new Operation(OperationType.EVENT_CREATION, event.id);
    event.operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(event.id, operation.id()));
    return event;
  }

  @Override
  public UUID getId() {
    return id;
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
    final Operation operation = new Operation(OperationType.NEW_EXPENSE, expense.label(), id);
    operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(id, operation.id()));
  }

  public void deleteExpense(UUID expenseId) {
    final Expense expense = find(expenseId);
    expenses.remove(expense);
    publishInternalEvent(new ExpenseDeletedInternalEvent(expense));
    final Operation operation = new Operation(OperationType.EXPENSE_DELETED, expense.label(), id);
    operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(id, operation.id()));
  }

  public void addParticipant(Participant participant) {
    participant.eventId(id);
    participants.add(participant);
    publishInternalEvent(new ParticipantAddedInternalEvent(id, participant));
    final Operation operation = new Operation(OperationType.NEW_PARTICIPANT, participant.name(), id);
    operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(id, operation.id()));
  }

  public Participant findParticipant(UUID participantId) {
    return participants.stream()
        .filter(participant -> participantId.equals(participant.id()))
        .findFirst()
        .orElseThrow(UnknownParticipant::new);
  }

  public void addOperation(Operation operation) {
    operations.add(operation);
    publishInternalEvent(new OperationPerformedInternalEvent(id, operation.id()));
  }

  private Expense find(UUID expenseId) {
    return expenses.stream()
        .filter(expense -> expense.id().equals(expenseId))
        .findFirst()
        .orElseThrow(UnknownExpense::new);
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
