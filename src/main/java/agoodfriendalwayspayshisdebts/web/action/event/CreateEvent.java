package agoodfriendalwayspayshisdebts.web.action.event;

import agoodfriendalwayspayshisdebts.command.event.CreateEventCommand;
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
public class CreateEvent {

  @Inject
  public CreateEvent(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events")
  public Payload create(CreateEventCommand command) {
    final ExecutionResult<UUID> result = commandBus.sendAndWaitResponse(command);
    if (!result.isSuccess()) {
      Throwables.propagate(result.error());
    }
    return new Payload(result.data()).withCode(HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
