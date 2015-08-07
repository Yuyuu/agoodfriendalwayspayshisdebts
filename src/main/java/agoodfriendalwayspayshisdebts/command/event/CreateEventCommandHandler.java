package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import com.vter.command.CommandHandler;

import java.util.UUID;

public class CreateEventCommandHandler implements CommandHandler<CreateEventCommand, UUID> {

  @Override
  public UUID execute(CreateEventCommand command) {
    Event event = new Event(command.name, command.participants);
    RepositoryLocator.events().save(event);
    return event.getId();
  }
}
