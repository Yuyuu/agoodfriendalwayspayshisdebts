package agoodfriendalwayspayshisdebts.search.expense.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    
    createExpensesDetailsIfNotPresentFor(event);

    final Map<UUID, String> eventParticipantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::id, Participant::name));
    final ExpenseDetails expenseDetails = createExpenseDetails(internalEvent.expense, eventParticipantsNames);

    jongo.getCollection("eventexpensesdetails_view")
        .update("{_id:#}", internalEvent.eventId)
        .with("{$push:{expenses:#}}", expenseDetails);
  }

  private void createExpensesDetailsIfNotPresentFor(Event event) {
    final long count = jongo.getCollection("eventexpensesdetails_view").count("{_id:#}", event.getId());
    if (count == 0) {
      jongo.getCollection("eventexpensesdetails_view").save(expensesDetailsOf(event.getId()));
    }
  }

  private static EventExpensesDetails expensesDetailsOf(UUID eventId) {
    return new EventExpensesDetails(eventId);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense, Map<UUID, String> eventParticipantsNames) {
    return ExpenseDetails.fromExpense(expense, eventParticipantsNames);
  }

  private final Jongo jongo;
}
