package agoodfriendalwayspayshisdebts.search.event.details.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
import agoodfriendalwayspayshisdebts.search.event.details.model.ExpenseDetails;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final EventDetails eventDetails = jongo.getCollection("eventdetails_view")
        .findOne("{_id:#}", internalEvent.eventId)
        .as(EventDetails.class);
    assert eventDetails != null;

    eventDetails.expenses.add(createExpenseDetails(internalEvent.expense));

    jongo.getCollection("eventdetails_view")
        .update("{_id:#}", eventDetails.id)
        .with("{$set:{expenses:#}}", eventDetails.expenses);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense) {
    return ExpenseDetails.fromExpense(expense);
  }

  private final Jongo jongo;
}
