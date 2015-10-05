package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
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
    final CalculationResult result = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(CalculationResult.class);
    assert result != null;

    result.deleteExpense(internalEvent.expense);
    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).with(result);
  }

  private final Jongo jongo;
}
