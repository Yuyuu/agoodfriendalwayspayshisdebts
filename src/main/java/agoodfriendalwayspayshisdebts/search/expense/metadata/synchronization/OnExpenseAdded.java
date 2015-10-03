package agoodfriendalwayspayshisdebts.search.expense.metadata.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.expense.metadata.model.ExpenseMetadata;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ExpenseAddedInternalEvent internalEvent) {
    final ExpenseMetadata expenseMetadata = ExpenseMetadata.forExpense(internalEvent.expense);
    jongo.getCollection("expensesmetadata_view")
        .update("{_id:#}", internalEvent.eventId)
        .upsert()
        .with("{$push:{metadata:#}}", expenseMetadata);
  }

  private final Jongo jongo;
}
