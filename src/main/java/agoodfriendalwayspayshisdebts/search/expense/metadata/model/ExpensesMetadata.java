package agoodfriendalwayspayshisdebts.search.expense.metadata.model;

import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;

public class ExpensesMetadata {
  @MongoId
  public UUID eventId;
  public List<ExpenseMetadata> metadata = Lists.newArrayList();

  @SuppressWarnings("unused")
  private ExpensesMetadata() {}
}
