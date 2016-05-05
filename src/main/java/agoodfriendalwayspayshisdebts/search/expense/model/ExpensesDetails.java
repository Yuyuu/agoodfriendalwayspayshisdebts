package agoodfriendalwayspayshisdebts.search.expense.model;

import java.util.List;

public class ExpensesDetails extends ExpensesSearchResult<ExpenseDetails> {

  @SuppressWarnings("unused")
  private ExpensesDetails() {}

  @SuppressWarnings("unused")
  public ExpensesDetails(int totalCount, List<ExpenseDetails> items) {
    super(totalCount, items);
  }
}
