package agoodfriendalwayspayshisdebts.command.reminder;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.model.reminder.ReminderState;
import com.vter.command.CommandHandler;
import com.vter.model.internal_event.InternalEventBus;

public class RecordReminderStateCommandHandler implements CommandHandler<RecordReminderStateCommand, Void> {

  @Override
  public Void execute(RecordReminderStateCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Participant participant = event.findParticipant(command.participantId);
    final OperationType operationType = operationType(command.event);
    InternalEventBus.INSTANCE().publish(
        new OperationPerformedInternalEvent(event.getId(), operationType, participant.name())
    );
    return null;
  }

  private OperationType operationType(String stateAsString) {
    final ReminderState state = ReminderState.parseFromStringInsensitive(stateAsString);
    return state.operationType();
  }
}
