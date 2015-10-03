package agoodfriendalwayspayshisdebts.search.expense.metadata.synchronization;

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
    jongo.getCollection("expensesmetadata_view")
        .update("{_id:#}", internalEvent.eventId)
        .with("{$pull:{metadata:{id:#}}}", internalEvent.expense.id());
  }

  private final Jongo jongo;
}
