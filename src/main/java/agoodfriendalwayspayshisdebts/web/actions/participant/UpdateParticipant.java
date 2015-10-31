package agoodfriendalwayspayshisdebts.web.actions.participant;

import agoodfriendalwayspayshisdebts.command.participant.UpdateParticipantCommand;
import com.google.common.base.Throwables;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.annotations.Put;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class UpdateParticipant {

  @Inject
  public UpdateParticipant(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Put("/events/:stringifiedEventUuid/participants/:stringifiedParticipantUuid")
  public Payload update(
      String stringifiedEventUuid, String stringifiedParticipantUuid, UpdateParticipantCommand command) {
    command.eventId = UUID.fromString(stringifiedEventUuid);
    command.id = UUID.fromString(stringifiedParticipantUuid);

    final ExecutionResult<Void> result = commandBus.sendAndWaitResponse(command);
    if (!result.isSuccess()) {
      Throwables.propagate(result.error());
    }
    return Payload.ok();
  }

  private final CommandBus commandBus;
}
