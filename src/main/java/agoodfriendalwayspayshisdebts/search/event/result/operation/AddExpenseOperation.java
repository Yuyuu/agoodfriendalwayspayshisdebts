package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.UUID;
import java.util.function.Consumer;

public class AddExpenseOperation extends ExpenseBasedResultOperation {

  public AddExpenseOperation(Expense expense) {
    super(expense);
  }

  @Override
  protected void applyOperation() {
    increaseAmountSpentByPurchaser();
    distributeParticipantsShares();
  }

  private void increaseAmountSpentByPurchaser() {
    participantsResults.get(expense.purchaserId()).increaseTotalAmountSpentBy(expense.amount());
  }

  private void distributeParticipantsShares() {
    expense.participantsIds().stream().filter(hasNotId(expense.purchaserId())).forEach(allocateShare());
  }

  private Consumer<UUID> allocateShare() {
    final double amountPerShare = amountPerShare();

    return participantId -> {
      final int participantShare = participantsResults.get(participantId).participantShare();
      final double amountDueAfterSplit = amountPerShare * participantShare;

      participantsResults.get(participantId).increaseRawDebtTowards(expense.purchaserId(), amountDueAfterSplit);

      mitigateDebtBetween(participantId, expense.purchaserId());
    };
  }
}
