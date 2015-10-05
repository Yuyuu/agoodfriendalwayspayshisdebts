package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.model.internal_event.InternalEvent;

public class ExpenseDeletedInternalEvent implements InternalEvent {

  public ExpenseDeletedInternalEvent(Expense expense) {
    this.expense = expense;
  }

  public final Expense expense;
}
