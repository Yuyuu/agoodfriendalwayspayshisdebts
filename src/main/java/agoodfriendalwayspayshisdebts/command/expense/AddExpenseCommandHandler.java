package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpenseDetails;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AddExpenseCommandHandler implements CommandHandler<AddExpenseCommand, ExpenseDetails> {

  @Override
  public ExpenseDetails execute(AddExpenseCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Expense expense = new Expense(
        command.label,
        toUuid(command.purchaserUuid),
        command.amount,
        toUuidsIfPresent(command.participantsUuids).orElseGet(participantsIds(event)),
        event.getId()
    );
    expense.setDescription(sanitize(command.description));
    event.addExpense(expense);

    event.addOperation(new Operation(OperationType.NEW_EXPENSE, expense.label(), event.getId()));

    final Map<UUID, String> eventParticipantsNames = event.participants().stream()
        .collect(Collectors.toMap(Participant::getId, Participant::name));
    return ExpenseDetails.forExpense(expense, eventParticipantsNames);
  }

  private static Optional<List<UUID>> toUuidsIfPresent(List<String> stringifiedUuids) {
    if (stringifiedUuids == null) {
      return Optional.empty();
    }
    return Optional.of(stringifiedUuids.stream().map(AddExpenseCommandHandler::toUuid).collect(Collectors.toList()));
  }

  private static UUID toUuid(String stringifiedUuid) {
    return UUID.fromString(stringifiedUuid);
  }

  private static Supplier<List<UUID>> participantsIds(Event event) {
    return () -> event.participants().stream().map(Participant::getId).collect(Collectors.toList());
  }

  private static String sanitize(String originalDescription) {
    return (originalDescription != null) ? originalDescription.trim() : "";
  }
}
