package agoodfriendalwayspayshisdebts.search.event.results.operation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;

import java.util.UUID;
import java.util.function.Consumer;

public class IncludeParticipantOperation extends ResultsOperation {

  public IncludeParticipantOperation(Expense expense, Participant participant) {
    this.expense = expense;
    this.participant = participant;
  }

  protected void applyOperation() {
    final int currentNumberOfShares = expense.participantsIds().stream()
        .map(id -> participantsResults.get(id).participantShare())
        .reduce(0, Integer::sum);

    expense.participantsIds().stream()
        .filter(hasNotId(participant.getId(), expense.purchaserId()))
        .forEach(restoreDebtSurplus(currentNumberOfShares));

    final double amount = expense.amount() / currentNumberOfShares * participant.share();
    participantsResults.get(participant.getId()).increaseRawDebtTowards(expense.purchaserId(), amount);
    mitigateDebtBetween(expense.purchaserId(), participant.getId());
  }

  private Consumer<UUID> restoreDebtSurplus(int currentNumberOfShares) {
    final int previousNumberOfShares = currentNumberOfShares - participant.share();

    return participantId -> {
      final int participantShare = participantsResults.get(participantId).participantShare();
      final double initialDebt = expense.amount() / previousNumberOfShares * participantShare;
      final double debtSurplus = initialDebt / currentNumberOfShares * participant.share();

      participantsResults.get(participantId).decreaseRawDebtTowards(expense.purchaserId(), debtSurplus);

      mitigateDebtBetween(expense.purchaserId(), participantId);
    };
  }

  private final Expense expense;
  private final Participant participant;
}
