package agoodfriendalwayspayshisdebts.web.actions.participant;

import agoodfriendalwayspayshisdebts.command.participant.AddParticipantCommand;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.web.actions.BaseAction;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class AddParticipant extends BaseAction {

  @Inject
  public AddParticipant(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/participants")
  public Payload add(String stringifiedUuid, AddParticipantCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<UUID> result = commandBus.sendAndWaitResponse(command);
    return getIdPayloadOrFail(result, HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
