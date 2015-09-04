package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OnExpenseDeleted implements InternalEventHandler<ExpenseDeletedInternalEvent> {

  @Inject
  public OnExpenseDeleted(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseDeletedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final CalculationResult result = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.eventId)
        .as(CalculationResult.class);

    assert result != null;

    final Map<UUID, Integer> expenseParticipantsShares = event.participants().stream()
        .filter(participatesIn(internalEvent.expense))
        .collect(Collectors.toMap(Participant::id, Participant::share));

    assert internalEvent.expense.participantsIds().size() == expenseParticipantsShares.size();

    result.deleteExpense(internalEvent.expense, expenseParticipantsShares);
    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).with(result);
  }

  private static Predicate<Participant> participatesIn(Expense expense) {
    return participant -> expense.participantsIds().contains(participant.id());
  }

  private final Jongo jongo;
}
