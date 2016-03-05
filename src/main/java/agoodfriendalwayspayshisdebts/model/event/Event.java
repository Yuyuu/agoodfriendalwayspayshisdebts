package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
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
  private String currency;
  private Set<Participant> participants = Sets.newHashSet();
  private List<Expense> expenses = Lists.newArrayList();

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Event() {}

  public Event(String name, String currency, Collection<Participant> participants) {
    this.name = name;
    this.currency = currency;
    participants.forEach(participant -> participant.eventId(this.getId()));
    this.participants.addAll(participants);
  }

  public static Event createAndPublishInternalEvent(String name, String currency, Collection<Participant> participants) {
    final Event event = new Event(name, currency, participants);
    publishInternalEvents(
        new EventCreatedInternalEvent(event.getId()),
        new OperationPerformedInternalEvent(event.getId(), OperationType.EVENT_CREATION, event.name())
    );
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

  public String currency() {
    return currency;
  }

  public void addExpense(Expense expense) {
    expenses.add(expense);
    publishInternalEvents(
        new ExpenseAddedInternalEvent(expense),
        new OperationPerformedInternalEvent(getId(), OperationType.NEW_EXPENSE, expense.label())
    );
  }

  public Expense deleteExpense(UUID expenseId) {
    final Expense expense = find(expenseId);
    expenses.remove(expense);
    publishInternalEvents(
        new ExpenseDeletedInternalEvent(expense),
        new OperationPerformedInternalEvent(getId(), OperationType.EXPENSE_DELETED, expense.label())
    );
    return expense;
  }

  public void addParticipant(Participant participant) {
    participant.eventId(getId());
    participants.add(participant);
    publishInternalEvents(
        new ParticipantAddedInternalEvent(getId(), participant),
        new OperationPerformedInternalEvent(getId(), OperationType.NEW_PARTICIPANT, participant.name())
    );
  }

  public Participant findParticipant(UUID participantId) {
    return participants.stream()
        .filter(participant -> participantId.equals(participant.getId()))
        .findFirst()
        .orElseThrow(UnknownParticipant::new);
  }

  private Expense find(UUID expenseId) {
    return expenses.stream()
        .filter(expense -> expense.getId().equals(expenseId))
        .findFirst()
        .orElseThrow(UnknownExpense::new);
  }

  private static <TInternalEvent extends InternalEvent> void publishInternalEvents(TInternalEvent ...internalEvents) {
    InternalEventBus.INSTANCE().publish(internalEvents);
  }
}
