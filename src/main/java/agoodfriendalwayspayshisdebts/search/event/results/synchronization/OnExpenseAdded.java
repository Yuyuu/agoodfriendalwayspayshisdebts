package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.operation.AddExpenseOperation;
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
    final EventResults result = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(EventResults.class);
    assert result != null;

    result.apply(new AddExpenseOperation(internalEvent.expense));

    jongo.getCollection("eventresults_view").update("{_id:#}", result.eventId).with(result);
  }

  private final Jongo jongo;
}
