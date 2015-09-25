package agoodfriendalwayspayshisdebts.search.expense.model;

import java.util.UUID;

public class ExpenseMetadata {
  public UUID id;
  public String label;

  public ExpenseMetadata(UUID id, String label) {
    this.id = id;
    this.label = label;
  }
}
