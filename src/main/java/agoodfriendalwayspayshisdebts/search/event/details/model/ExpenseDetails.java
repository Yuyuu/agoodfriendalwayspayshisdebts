package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.List;
import java.util.UUID;

public class ExpenseDetails {
  public String label;
  public UUID purchaserId;
  public double amount;
  public List<UUID> participantsIds;
  public String description;

  public ExpenseDetails() {}

  public static ExpenseDetails fromExpense(Expense expense) {
    final ExpenseDetails expenseDetails = new ExpenseDetails();
    expenseDetails.label = expense.label();
    expenseDetails.purchaserId = expense.purchaserId();
    expenseDetails.amount = expense.amount();
    expenseDetails.participantsIds = expense.participantsIds();
    expenseDetails.description = expense.description();
    return expenseDetails;
  }
}
