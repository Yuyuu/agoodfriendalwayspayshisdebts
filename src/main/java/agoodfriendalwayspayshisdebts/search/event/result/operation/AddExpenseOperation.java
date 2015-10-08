package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.UUID;
import java.util.function.Consumer;

public class AddExpenseOperation extends ResultOperation {

  public AddExpenseOperation(Expense expense) {
    this.expense = expense;
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

  private double amountPerShare() {
    final int expenseNumberOfShares = expense.participantsIds().stream()
        .map(id -> participantsResults.get(id).participantShare())
        .reduce(0, Integer::sum);
    return expense.amount() / expenseNumberOfShares;
  }

  private final Expense expense;
}
