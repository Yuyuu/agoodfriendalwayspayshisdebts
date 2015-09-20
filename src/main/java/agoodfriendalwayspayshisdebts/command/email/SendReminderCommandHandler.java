package agoodfriendalwayspayshisdebts.command.email;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SendReminderCommandHandler implements CommandHandler<SendReminderCommand, Iterable<RecipientReport>> {

  @Override
  public Iterable<RecipientReport> execute(SendReminderCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);

    final List<UUID> recipientsIds = toUuids(command.recipientsUuids);

    return event.participants().stream()
        .filter(participant -> recipientsIds.contains(participant.id()))
        .map(participant -> RecipientReport.success(participant.name()))
        .collect(Collectors.toList());
  }

  private static List<UUID> toUuids(List<String> stringifiedUuids) {
    return stringifiedUuids.stream().map(UUID::fromString).collect(Collectors.toList());
  }
}
