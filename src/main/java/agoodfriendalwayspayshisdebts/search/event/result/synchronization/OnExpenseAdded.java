package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
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
    final Optional<CalculationResult> optionalResult = Optional.ofNullable(
        jongo.getCollection("eventresult_view").findOne("{_id:#}", internalEvent.eventId).as(CalculationResult.class)
    );
    final CalculationResult result = optionalResult.orElseGet(calculationResultOf(internalEvent.eventId));

    result.shareExpenseBetweenParticipants(internalEvent.expense);
    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).upsert().with(result);
  }

  private static Supplier<CalculationResult> calculationResultOf(UUID eventId) {
    return () -> {
      final Event event = RepositoryLocator.events().get(eventId);
      return CalculationResult.forEvent(event);
    };
  }

  private final Jongo jongo;
}
