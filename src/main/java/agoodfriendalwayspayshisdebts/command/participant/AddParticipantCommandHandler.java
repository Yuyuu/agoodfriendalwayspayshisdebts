package agoodfriendalwayspayshisdebts.command.participant;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddParticipantCommandHandler implements CommandHandler<AddParticipantCommand, UUID> {

  @Override
  public UUID execute(AddParticipantCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final String email = Optional.ofNullable(command.email).orElse("");
    final Participant participant = new Participant(command.name, command.share, email);
    event.addParticipant(participant);

    if (command.expensesUuids != null) {
      final List<UUID> expensesIds = toUuids(command.expensesUuids);
      final Map<UUID, Expense> expenses = expenses(event);
      expensesIds.stream().map(expenses::get).forEach(includeParticipant(participant));
    }

    event.addOperation(new Operation(OperationType.NEW_PARTICIPANT, participant.name(), event.getId()));
    return participant.getId();
  }

  private static List<UUID> toUuids(List<String> expensesUuids) {
    return expensesUuids.stream().map(UUID::fromString).collect(Collectors.toList());
  }

  private static Map<UUID, Expense> expenses(Event event) {
    return event.expenses().stream().collect(Collectors.toMap(Expense::getId, Function.identity()));
  }

  private static Consumer<Expense> includeParticipant(Participant participant) {
    return expense -> expense.includeParticipant(participant);
  }
}
