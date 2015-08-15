package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.model.internal_event.InternalEvent;

import java.util.UUID;

public class ExpenseAddedInternalEvent implements InternalEvent {

  public ExpenseAddedInternalEvent(UUID eventId, Expense expense) {
    this.eventId = eventId;
    this.expense = expense;
  }

  public final UUID eventId;
  public final Expense expense;
}
