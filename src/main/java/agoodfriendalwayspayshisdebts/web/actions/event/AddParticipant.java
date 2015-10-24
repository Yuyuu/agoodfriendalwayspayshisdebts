package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.command.event.AddParticipantCommand;
import com.google.common.base.Throwables;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class AddParticipant {

  @Inject
  public AddParticipant(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/participants")
  public Payload add(String stringifiedUuid, AddParticipantCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);

    final ExecutionResult<UUID> result = commandBus.sendAndWaitResponse(command);
    if (!result.isSuccess()) {
      Throwables.propagate(result.error());
    }

    return new Payload(new ParticipantIdJsonObject(result.data())).withCode(HttpStatus.CREATED);
  }

  private final CommandBus commandBus;

  private static class ParticipantIdJsonObject {
    public UUID id;

    public ParticipantIdJsonObject(UUID id) {
      this.id = id;
    }
  }
}
