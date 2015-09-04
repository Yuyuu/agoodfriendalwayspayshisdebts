package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.command.expense.DeleteExpenseCommand;
import com.google.common.base.Throwables;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.annotations.Delete;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class DeleteExpense {

  @Inject
  public DeleteExpense(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Delete("/events/:stringifiedEventUuid/expenses/:stringifiedExpenseUuid")
  public Payload delete(String stringifiedEventId, String stringifiedExpenseUuid) {
    final DeleteExpenseCommand command = new DeleteExpenseCommand();
    command.eventId = UUID.fromString(stringifiedEventId);
    command.expenseId = UUID.fromString(stringifiedExpenseUuid);

    final ExecutionResult<Void> result = commandBus.sendAndWaitResponse(command);
    if (!result.isSuccess()) {
      Throwables.propagate(result.error());
    }
    return new Payload(HttpStatus.NO_CONTENT);
  }

  private final CommandBus commandBus;
}
