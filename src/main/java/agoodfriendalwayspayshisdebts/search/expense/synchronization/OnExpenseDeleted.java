package agoodfriendalwayspayshisdebts.search.expense.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnExpenseDeleted implements InternalEventHandler<ExpenseDeletedInternalEvent> {

  @Inject
  public OnExpenseDeleted(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ExpenseDeletedInternalEvent internalEvent) {
    jongo.getCollection("expensesdetails_view")
        .update("{_id:#}", internalEvent.expense.eventId())
        .with("{$inc:{expenseCount:-1},$pull:{expenses:{id:#}}}", internalEvent.expense.getId());
  }

  private final Jongo jongo;
}
