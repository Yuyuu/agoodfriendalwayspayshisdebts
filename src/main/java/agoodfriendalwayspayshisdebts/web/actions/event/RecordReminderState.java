package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.command.reminder.RecordReminderStateCommand;
import com.vter.command.CommandBus;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;

@Resource
public class RecordReminderState {

  @Inject
  public RecordReminderState(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/hooks")
  public Payload post(RecordReminderStateCommand command) {
    commandBus.send(command);
    return Payload.ok();
  }

  private final CommandBus commandBus;
}
