package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import org.mongolink.domain.mapper.AggregateMap;

/* This is implicitly used by MongoLink */
@SuppressWarnings("unused")
public class ExpenseMapping extends AggregateMap<Expense> {

  @Override
  public void map() {
    property().onField("id");
    property().onField("label");
    property().onField("purchaserId");
    property().onField("amount");
    collection().onField("participantsIds");
    property().onField("description");
    property().onField("eventId");
  }
}
