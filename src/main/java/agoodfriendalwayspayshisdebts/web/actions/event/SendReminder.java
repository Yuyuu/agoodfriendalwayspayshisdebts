package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.command.event.SendReminderCommand;
import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import com.google.common.util.concurrent.ListenableFuture;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.Cookies;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class SendReminder {

  @Inject
  public SendReminder(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/reminder")
  public Payload send(String stringifiedUuid, Cookies cookies, SendReminderCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);
    command.locale = cookies.value("i18next");

    final ListenableFuture<ExecutionResult<Iterable<RecipientReport>>> future = commandBus.send(command);

    return new Payload(future).withCode(HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
