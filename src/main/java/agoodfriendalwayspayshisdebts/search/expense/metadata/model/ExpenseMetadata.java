package agoodfriendalwayspayshisdebts.search.expense.metadata.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.UUID;

public class ExpenseMetadata {
  public UUID id;
  public String label;

  @SuppressWarnings("unused")
  private ExpenseMetadata() {}

  public static ExpenseMetadata forExpense(Expense expense) {
    final ExpenseMetadata expenseMetadata = new ExpenseMetadata();
    expenseMetadata.id = expense.id();
    expenseMetadata.label = expense.label();
    return expenseMetadata;
  }
}
