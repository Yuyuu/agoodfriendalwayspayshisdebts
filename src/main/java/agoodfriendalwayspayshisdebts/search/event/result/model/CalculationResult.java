package agoodfriendalwayspayshisdebts.search.event.result.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Maps;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CalculationResult {
  private static final double NO_DEBT = 0D;

  @MongoId
  public UUID eventId;
  public Map<UUID, ParticipantResult> participantsResults = Maps.newHashMap();

  @SuppressWarnings("unused")
  private CalculationResult() {}

  private CalculationResult(UUID eventId) {
    this.eventId = eventId;
  }

  public static CalculationResult forEvent(Event event) {
    final CalculationResult result = new CalculationResult(event.getId());
    final List<UUID> participantsIds = event.participants().stream()
        .map(Participant::id)
        .collect(Collectors.toList());

    event.participants().forEach(participant -> {
      final ParticipantResult participantResult = ParticipantResult.forParticipantId(participant.id(), participantsIds);
      result.participantsResults.put(participant.id(), participantResult);
    });

    return result;
  }

  public void shareExpenseBetweenParticipants(Expense expense, Map<UUID, Integer> participantsShares) {
    final UUID purchaserId = expense.purchaserId();
    final int expenseNumberOfShares = participantsShares.values().stream().reduce(0, Integer::sum);
    final double amountDuePerShare = expense.amount() / expenseNumberOfShares;

    increaseAmountSpentByPurchaser(purchaserId, expense.amount());

    expense.participantsIds().stream()
        .filter(hasNotId(purchaserId))
        .forEach(allocateParticipantShare(participantsShares, purchaserId, amountDuePerShare));
  }

  private Consumer<UUID> allocateParticipantShare(Map<UUID, Integer> participantsShares,
                                                  UUID purchaserId,
                                                  double amountDuePerShare) {
    return id -> {
      final int participantShare = participantsShares.get(id);
      final double amountDueAfterSplit = amountDuePerShare * participantShare;
      final double currentDebtTowardsPurchaser = participantsResults.get(id).debtTowards(purchaserId);
      final double updatedDebtTowardsPurchaser = currentDebtTowardsPurchaser + amountDueAfterSplit;
      final double debtTowardsParticipant = participantsResults.get(purchaserId).debtTowards(id);

      final double mitigatedDebt = Math.abs(updatedDebtTowardsPurchaser - debtTowardsParticipant);

      if (updatedDebtTowardsPurchaser > debtTowardsParticipant) {
        participantsResults.get(id).updateDebtTowards(purchaserId, mitigatedDebt);
        participantsResults.get(purchaserId).updateDebtTowards(id, NO_DEBT);
      } else if (updatedDebtTowardsPurchaser < debtTowardsParticipant) {
        participantsResults.get(id).updateDebtTowards(purchaserId, NO_DEBT);
        participantsResults.get(purchaserId).updateDebtTowards(id, mitigatedDebt);
      } else {
        participantsResults.get(id).updateDebtTowards(purchaserId, NO_DEBT);
        participantsResults.get(purchaserId).updateDebtTowards(id, NO_DEBT);
      }
    };
  }

  private void increaseAmountSpentByPurchaser(UUID purchaserId, double amount) {
    participantsResults.get(purchaserId).increaseTotalAmountSpentBy(amount);
  }

  private static Predicate<UUID> hasNotId(UUID purchaserId) {
    return participantId -> !participantId.equals(purchaserId);
  }
}
