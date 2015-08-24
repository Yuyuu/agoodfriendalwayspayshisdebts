package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final Optional<CalculationResult> optionalResult = Optional.ofNullable(
        jongo.getCollection("eventresult_view").findOne("{_id:#}", internalEvent.eventId).as(CalculationResult.class)
    );
    final CalculationResult result = optionalResult.orElseGet(calculationResultOf(event));

    final Map<UUID, Integer> expenseParticipantsShares = event.participants().stream()
        .filter(participatesIn(internalEvent.expense))
        .collect(Collectors.toMap(Participant::id, Participant::share));

    assert internalEvent.expense.participantsIds().size() == expenseParticipantsShares.size();

    result.shareExpenseBetweenParticipants(internalEvent.expense, expenseParticipantsShares);

    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).upsert().with(result);
  }

  private static Predicate<Participant> participatesIn(Expense expense) {
    return participant -> expense.participantsIds().contains(participant.id());
  }

  private static Supplier<CalculationResult> calculationResultOf(Event event) {
    return () -> CalculationResult.forEvent(event);
  }

  private final Jongo jongo;
}
