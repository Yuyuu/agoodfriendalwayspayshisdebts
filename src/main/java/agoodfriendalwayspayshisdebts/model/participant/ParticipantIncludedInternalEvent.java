package agoodfriendalwayspayshisdebts.model.participant;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.vter.model.internal_event.InternalEvent;

public class ParticipantIncludedInternalEvent implements InternalEvent {

  public ParticipantIncludedInternalEvent(Expense expense, Participant participant) {
    this.expense = expense;
    this.participant = participant;
  }

  public final Expense expense;
  public final Participant participant;
}
