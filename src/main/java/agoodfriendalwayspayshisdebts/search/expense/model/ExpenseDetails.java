package agoodfriendalwayspayshisdebts.search.expense.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExpenseDetails {
  @MongoId
  public UUID id;
  public String label;
  public String state;
  public String purchaserName;
  public double amount;
  public List<String> participantsNames = Lists.newArrayList();
  public String description;
  public DateTime creationDate;
  public UUID eventId;

  private ExpenseDetails() {}

  public static ExpenseDetails forExpense(Expense expense, Map<UUID, String> eventParticipantsNames) {
    final ExpenseDetails expenseDetails = new ExpenseDetails();
    expenseDetails.id = expense.getId();
    expenseDetails.label = expense.label();
    expenseDetails.state = expense.state().toString();
    expenseDetails.purchaserName = eventParticipantsNames.get(expense.purchaserId());
    expenseDetails.amount = expense.amount();
    expenseDetails.participantsNames.addAll(
        expense.participantsIds().stream().map(eventParticipantsNames::get).collect(Collectors.toList())
    );
    expenseDetails.description = expense.description();
    expenseDetails.creationDate = expense.creationDate();
    expenseDetails.eventId = expense.eventId();
    return expenseDetails;
  }
}
