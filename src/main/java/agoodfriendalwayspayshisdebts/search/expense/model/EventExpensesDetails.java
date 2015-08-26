package agoodfriendalwayspayshisdebts.search.expense.model;

import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;

public class EventExpensesDetails {
  @MongoId
  public UUID eventId;
  public List<ExpenseDetails> expenses = Lists.newArrayList();

  @SuppressWarnings("unused")
  private EventExpensesDetails() {}

  public EventExpensesDetails(UUID eventId) {
    this.eventId = eventId;
  }
}
