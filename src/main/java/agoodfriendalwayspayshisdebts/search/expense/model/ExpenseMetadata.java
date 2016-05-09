package agoodfriendalwayspayshisdebts.search.expense.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.UUID;

public class ExpenseMetadata {
  @MongoId
  public UUID id;
  public String label;

  @SuppressWarnings("unused")
  private ExpenseMetadata() {}

  public static ExpenseMetadata forExpense(Expense expense) {
    final ExpenseMetadata expenseMetadata = new ExpenseMetadata();
    expenseMetadata.id = expense.getId();
    expenseMetadata.label = expense.label();
    return expenseMetadata;
  }
}
