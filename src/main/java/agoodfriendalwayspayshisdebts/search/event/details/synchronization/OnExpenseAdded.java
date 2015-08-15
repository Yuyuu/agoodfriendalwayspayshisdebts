package agoodfriendalwayspayshisdebts.search.event.details.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
import agoodfriendalwayspayshisdebts.search.event.details.model.ExpenseDetails;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Supplier;

public class OnExpenseAdded implements InternalEventHandler<ExpenseAddedInternalEvent> {

  @Inject
  public OnExpenseAdded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeEvent(ExpenseAddedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final Optional<EventDetails> optionalEventDetails = Optional.ofNullable(
        jongo.getCollection("eventdetails_view").findOne("{_id:#}", internalEvent.eventId).as(EventDetails.class)
    );

    EventDetails eventDetails = optionalEventDetails.orElseGet(createEventDetails(event));
    eventDetails.expenses.add(createExpenseDetails(internalEvent.expense));

    final int documentsAffected = jongo.getCollection("eventdetails_view")
        .update("{_id:#}", eventDetails.id)
        .upsert()
        .with(eventDetails).getN();
    assert documentsAffected == 1;
  }

  private static Supplier<EventDetails> createEventDetails(Event event) {
    return () -> EventDetails.fromEvent(event);
  }

  private static ExpenseDetails createExpenseDetails(Expense expense) {
    return ExpenseDetails.fromExpense(expense);
  }

  private final Jongo jongo;
}
