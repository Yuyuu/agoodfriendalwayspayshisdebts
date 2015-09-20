package agoodfriendalwayspayshisdebts.command.email;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import com.vter.command.CommandHandler;

import java.util.stream.Collectors;

public class SendReminderCommandHandler implements CommandHandler<SendReminderCommand, Iterable<RecipientReport>> {

  @Override
  public Iterable<RecipientReport> execute(SendReminderCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);

    return event.participants().stream()
        .map(participant -> RecipientReport.success(participant.name()))
        .collect(Collectors.toList());
  }
}
