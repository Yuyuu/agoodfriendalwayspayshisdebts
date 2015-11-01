package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.operation.DeleteExpenseOperation;
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
    final EventResults result = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(EventResults.class);
    assert result != null;

    result.apply(new DeleteExpenseOperation(internalEvent.expense));

    jongo.getCollection("eventresults_view").update("{_id:#}", result.eventId).with(result);
  }

  private final Jongo jongo;
}
