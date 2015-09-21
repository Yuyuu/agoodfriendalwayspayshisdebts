package agoodfriendalwayspayshisdebts.web.actions.event;

import agoodfriendalwayspayshisdebts.command.email.SendReminderCommand;
import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.Cookies;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Resource
public class SendReminder {

  @Inject
  public SendReminder(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/reminder")
  public Payload send(Cookies cookies, String stringifiedUuid, SendReminderCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);
    command.locale = cookies.value("i18next");

    final CompletableFuture<ExecutionResult<Iterable<RecipientReport>>> future = commandBus.send(command);

    return new Payload(future).withCode(HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
