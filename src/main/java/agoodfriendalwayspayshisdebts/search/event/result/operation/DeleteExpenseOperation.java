package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.UUID;
import java.util.function.Consumer;

public class DeleteExpenseOperation extends ExpenseBasedResultOperation {

  public DeleteExpenseOperation(Expense expense) {
    super(expense);
  }

  @Override
  protected void applyOperation() {
    decreaseAmountSpentByPurchaser();
    restoreParticipantsShares();
  }

  private void decreaseAmountSpentByPurchaser() {
    participantsResults.get(expense.purchaserId()).decreaseTotalAmountSpentBy(expense.amount());
  }

  private void restoreParticipantsShares() {
    expense.participantsIds().stream().filter(hasNotId(expense.purchaserId())).forEach(restoreShare());
  }

  private Consumer<UUID> restoreShare() {
    final double amountRestoredPerShare = amountPerShare();

    return participantId -> {
      final int participantShare = participantsResults.get(participantId).participantShare();
      final double amountRestoredAfterSplit = amountRestoredPerShare * participantShare;

      participantsResults.get(participantId).decreaseRawDebtTowards(expense.purchaserId(), amountRestoredAfterSplit);

      mitigateDebtBetween(participantId, expense.purchaserId());
    };
  }
}
