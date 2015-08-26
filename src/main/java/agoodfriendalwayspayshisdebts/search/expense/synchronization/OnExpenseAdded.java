package agoodfriendalwayspayshisdebts.search.expense.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails;
import agoodfriendalwayspayshisdebts.search.expense.model.EventExpensesDetails;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final Optional<EventExpensesDetails> optionalExpensesDetails = Optional.ofNullable(
        jongo.getCollection("eventexpensesdetails_view")
            .findOne("{_id:#}", internalEvent.eventId)
            .as(EventExpensesDetails.class)
    );
    final EventExpensesDetails expensesDetails = optionalExpensesDetails
        .orElseGet(expensesDetailsOf(internalEvent.eventId));

    expensesDetails.expenses.add(createExpenseDetails(internalEvent.expense));

    jongo.getCollection("eventexpensesdetails_view")
        .update("{_id:#}", expensesDetails.eventId)
        .upsert()
        .with(expensesDetails);
  }

  private Supplier<EventExpensesDetails> expensesDetailsOf(UUID eventId) {
    return () -> new EventExpensesDetails(eventId);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense) {
    return ExpenseDetails.fromExpense(expense);
  }

  private final Jongo jongo;
}
