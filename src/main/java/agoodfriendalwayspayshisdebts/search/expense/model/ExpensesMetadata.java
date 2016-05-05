package agoodfriendalwayspayshisdebts.search.expense.model;

import java.util.List;

public class ExpensesMetadata extends ExpensesSearchResult<ExpenseMetadata> {

  @SuppressWarnings("unused")
  private ExpensesMetadata() {}

  @SuppressWarnings("unused")
  public ExpensesMetadata(int totalCount, List<ExpenseMetadata> items) {
    super(totalCount, items);
  }
}
