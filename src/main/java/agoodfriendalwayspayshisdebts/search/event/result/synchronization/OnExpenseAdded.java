package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.calculation.ResultCalculator;
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
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);

    final ResultCalculator resultCalculator = new ResultCalculator(event);
    final CalculationResult result = resultCalculator.calculate();

    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).upsert().with(result);
  }

  private final Jongo jongo;
}
