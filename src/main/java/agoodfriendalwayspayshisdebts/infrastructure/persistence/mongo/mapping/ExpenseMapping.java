package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import org.mongolink.domain.mapper.ComponentMap;

/* This is implicitly used by MongoLink */
@SuppressWarnings("unused")
public class ExpenseMapping extends ComponentMap<Expense> {

  @Override
  public void map() {
    property().onProperty(Expense::getId);
    property().onField("label");
    property().onField("purchaserId");
    property().onField("amount");
    collection().onField("participantsIds");
    property().onField("description");
    property().onField("eventId");
  }
}
