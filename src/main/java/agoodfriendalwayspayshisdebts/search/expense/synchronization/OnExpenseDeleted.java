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
        .update("{_id:#,'items.id':#}", internalEvent.expense.eventId(), internalEvent.expense.getId())
        .with("{$set:{items.$.state:#}}", internalEvent.expense.state());
  }

  private final Jongo jongo;
}
