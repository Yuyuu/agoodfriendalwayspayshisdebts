package agoodfriendalwayspayshisdebts.search.event.result.calculation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class AmountSpentCalculator {

  public AmountSpentCalculator(List<Expense> expenses) {
    this.expenses = expenses;
  }

  public Map<UUID, Double> calculate() {
    return expenses.stream().collect(Collectors.toMap(Expense::purchaserId, Expense::amount, addAmounts()));
  }

  private static BinaryOperator<Double> addAmounts() {
    return (currentAmount, expenseAmount) -> currentAmount + expenseAmount;
  }

  private final List<Expense> expenses;
}
