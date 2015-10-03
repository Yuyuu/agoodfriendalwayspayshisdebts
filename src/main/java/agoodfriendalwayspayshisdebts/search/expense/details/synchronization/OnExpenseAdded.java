package agoodfriendalwayspayshisdebts.search.expense.details.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpenseDetails;
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
  public void executeInternalEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);

    final Map<UUID, String> eventParticipantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::id, Participant::name));
    final ExpenseDetails expenseDetails = createExpenseDetails(internalEvent.expense, eventParticipantsNames);

    jongo.getCollection("eventexpensesdetails_view")
        .update("{_id:#}", internalEvent.eventId)
        .upsert()
        .with("{$inc:{expenseCount:1},$push:{expenses:#}}", expenseDetails);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense, Map<UUID, String> eventParticipantsNames) {
    return ExpenseDetails.fromExpense(expense, eventParticipantsNames);
  }

  private final Jongo jongo;
}
