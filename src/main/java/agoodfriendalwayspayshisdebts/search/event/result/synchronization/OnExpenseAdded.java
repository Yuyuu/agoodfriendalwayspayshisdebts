package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
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
    final CalculationResult result = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(CalculationResult.class);
    assert result != null;

    result.shareExpenseBetweenParticipants(internalEvent.expense);
    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).upsert().with(result);
  }

  private final Jongo jongo;
}
