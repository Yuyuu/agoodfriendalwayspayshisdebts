package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AddExpenseCommandHandler implements CommandHandler<AddExpenseCommand, Void> {

  @Override
  public Void execute(AddExpenseCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Expense expense = new Expense(
        command.label,
        toUuid(command.purchaserUuid),
        command.amount,
        toUuidsIfPresent(command.participantsUuids).orElseGet(participantsIds(event))
    );
    expense.setDescription(command.description);
    event.addExpense(expense);
    return null;
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
    return () -> event.participants().stream().map(Participant::id).collect(Collectors.toList());
  }
}
