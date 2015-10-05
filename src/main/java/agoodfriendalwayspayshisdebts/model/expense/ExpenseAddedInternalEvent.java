package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.model.internal_event.InternalEvent;

public class ExpenseAddedInternalEvent implements InternalEvent {

  public ExpenseAddedInternalEvent(Expense expense) {
    this.expense = expense;
  }

  public final Expense expense;
}
