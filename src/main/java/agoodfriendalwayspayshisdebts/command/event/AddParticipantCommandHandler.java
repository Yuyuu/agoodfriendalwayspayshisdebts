package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddParticipantCommandHandler implements CommandHandler<AddParticipantCommand, Void> {

  @Override
  public Void execute(AddParticipantCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Participant participant = new Participant(command.name, command.share, command.email);
    final List<UUID> expensesIds = toUuids(command.expensesUuids);
    event.includeParticipant(participant, expensesIds);
    return null;
  }

  private static List<UUID> toUuids(List<String> expensesUuids) {
    return expensesUuids.stream().map(UUID::fromString).collect(Collectors.toList());
  }
}
