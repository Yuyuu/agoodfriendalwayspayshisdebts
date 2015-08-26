package agoodfriendalwayspayshisdebts.search.expense.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExpenseDetails {
  public String label;
  public String purchaserName;
  public double amount;
  public List<String> participantsNames = Lists.newArrayList();
  public String description;

  private ExpenseDetails() {}

  public static ExpenseDetails fromExpense(Expense expense, Map<UUID, String> eventParticipantsNames) {
    final ExpenseDetails expenseDetails = new ExpenseDetails();
    expenseDetails.label = expense.label();
    expenseDetails.purchaserName = eventParticipantsNames.get(expense.purchaserId());
    expenseDetails.amount = expense.amount();
    expenseDetails.participantsNames.addAll(
        expense.participantsIds().stream().map(eventParticipantsNames::get).collect(Collectors.toList())
    );
    expenseDetails.description = expense.description();
    return expenseDetails;
  }
}
