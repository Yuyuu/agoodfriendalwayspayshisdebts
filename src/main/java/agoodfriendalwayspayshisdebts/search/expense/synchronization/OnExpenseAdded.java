package agoodfriendalwayspayshisdebts.search.expense.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
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
  public void executeInternalEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.expense.eventId());

    final Map<UUID, String> eventParticipantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::getId, Participant::name));
    final ExpenseDetails expenseDetails = createExpenseDetails(internalEvent.expense, eventParticipantsNames);

    jongo.getCollection("expensesdetails_view").insert(expenseDetails);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense, Map<UUID, String> eventParticipantsNames) {
    return ExpenseDetails.forExpense(expense, eventParticipantsNames);
  }

  private final Jongo jongo;
}
