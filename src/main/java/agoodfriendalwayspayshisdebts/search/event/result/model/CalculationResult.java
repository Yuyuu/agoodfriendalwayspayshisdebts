package agoodfriendalwayspayshisdebts.search.event.result.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.operation.ResultOperation;
import com.google.common.collect.Maps;
import org.jongo.marshall.jackson.oid.MongoId;

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
    final Map<UUID, String> participantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::id, Participant::name));

    event.participants().forEach(participant -> {
      final ParticipantResult participantResult = ParticipantResult.forParticipant(participant, participantsNames);
      result.participantsResults.put(participant.id(), participantResult);
    });

    return result;
  }

  public void shareExpenseBetweenParticipants(Expense expense) {
    final double amountDuePerShare = amountPerShareFor(expense);
    final Consumer<UUID> allocateParticipantShare = allocateParticipantShare(expense.purchaserId(), amountDuePerShare);

    increaseAmountSpentByPurchaser(expense.purchaserId(), expense.amount());
    forEachParticipantOf(expense, allocateParticipantShare);
  }

  public void deleteExpense(Expense expense) {
    final double amountRestoredPerShare = amountPerShareFor(expense);
    final Consumer<UUID> restoreParticipantShare = restoreParticipantShare(
        expense.purchaserId(),
        amountRestoredPerShare
    );

    decreaseAmountSpentByPurchaser(expense.purchaserId(), expense.amount());
    forEachParticipantOf(expense, restoreParticipantShare);
  }

  private double amountPerShareFor(Expense expense) {
    final int expenseNumberOfShares = expense.participantsIds().stream()
        .map(id -> participantsResults.get(id).participantShare())
        .reduce(0, Integer::sum);
    return expense.amount() / expenseNumberOfShares;
  }

  private static void forEachParticipantOf(Expense expense, Consumer<UUID> applyOperation) {
    expense.participantsIds().stream().filter(hasNotId(expense.purchaserId())).forEach(applyOperation);
  }

  private Consumer<UUID> restoreParticipantShare(UUID purchaserId, double amountRestoredPerShare) {
    return id -> {
      final int participantShare = participantsResults.get(id).participantShare();
      final double amountRestoredAfterSplit = amountRestoredPerShare * participantShare;

      participantsResults.get(id).decreaseRawDebtTowards(purchaserId, amountRestoredAfterSplit);

      final double rawDebtTowardsPurchaser = participantsResults.get(id).rawDebtTowards(purchaserId);
      final double rawDebtTowardsParticipant = participantsResults.get(purchaserId).rawDebtTowards(id);

      mitigateDebtBetween(id, purchaserId, rawDebtTowardsPurchaser, rawDebtTowardsParticipant);
    };
  }

  private Consumer<UUID> allocateParticipantShare(UUID purchaserId, double amountDuePerShare) {
    return id -> {
      final int participantShare = participantsResults.get(id).participantShare();
      final double amountDueAfterSplit = amountDuePerShare * participantShare;
      final double currentDebtTowardsPurchaser = participantsResults.get(id).mitigatedDebtTowards(purchaserId);
      final double updatedDebtTowardsPurchaser = currentDebtTowardsPurchaser + amountDueAfterSplit;
      final double debtTowardsParticipant = participantsResults.get(purchaserId).mitigatedDebtTowards(id);

      participantsResults.get(id).increaseRawDebtTowards(purchaserId, amountDueAfterSplit);

      mitigateDebtBetween(id, purchaserId, updatedDebtTowardsPurchaser, debtTowardsParticipant);
    };
  }

  private void mitigateDebtBetween(UUID aId, UUID bId, double aDebtTowardsB, double bDebtTowardsA) {
    final double mitigatedDebt = Math.abs(aDebtTowardsB - bDebtTowardsA);

    if (aDebtTowardsB > bDebtTowardsA) {
      participantsResults.get(aId).updateDebtTowards(bId, mitigatedDebt);
      participantsResults.get(bId).updateDebtTowards(aId, NO_DEBT);
    } else if (aDebtTowardsB < bDebtTowardsA) {
      participantsResults.get(aId).updateDebtTowards(bId, NO_DEBT);
      participantsResults.get(bId).updateDebtTowards(aId, mitigatedDebt);
    } else {
      participantsResults.get(aId).updateDebtTowards(bId, NO_DEBT);
      participantsResults.get(bId).updateDebtTowards(aId, NO_DEBT);
    }
  }

  private void increaseAmountSpentByPurchaser(UUID purchaserId, double amount) {
    participantsResults.get(purchaserId).increaseTotalAmountSpentBy(amount);
  }

  private void decreaseAmountSpentByPurchaser(UUID purchaserId, double amount) {
    participantsResults.get(purchaserId).decreaseTotalAmountSpentBy(amount);
  }

  private static Predicate<UUID> hasNotId(UUID purchaserId) {
    return participantId -> !participantId.equals(purchaserId);
  }

  public void apply(ResultOperation operation) {
    operation.accept(this);
  }
}
