package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.model.internal_event.InternalEvent;

import java.util.UUID;

public class ExpenseDeletedInternalEvent implements InternalEvent {

  public ExpenseDeletedInternalEvent(UUID eventId, Expense expense) {
    this.eventId = eventId;
    this.expense = expense;
  }

  public final UUID eventId;
  public final Expense expense;
}
