package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.CommandHandler;

public class UpdateParticipantCommandHandler implements CommandHandler<UpdateParticipantCommand, Void> {

  @Override
  public Void execute(UpdateParticipantCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Participant participant = event.findParticipant(command.id);
    participant.update(command.email);
    return null;
  }
}
