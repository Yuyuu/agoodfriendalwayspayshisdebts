package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.command.event.CreateEventCommand;
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
public class CreateEvent extends BaseAction {

  @Inject
  public CreateEvent(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events")
  public Payload create(CreateEventCommand command) {
    final ExecutionResult<UUID> result = commandBus.sendAndWaitResponse(command);
    return getIdPayloadOrFail(result, HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
