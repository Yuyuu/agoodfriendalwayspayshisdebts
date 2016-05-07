package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.command.expense.DeleteExpenseCommand;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.web.actions.BaseAction;
import net.codestory.http.annotations.Put;
import net.codestory.http.annotations.Resource;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class DeleteExpense extends BaseAction {

  @Inject
  public DeleteExpense(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Put("/events/:stringifiedEventUuid/expenses/:stringifiedExpenseUuid")
  public Payload delete(String stringifiedEventId, String stringifiedExpenseUuid) {
    final DeleteExpenseCommand command = new DeleteExpenseCommand();
    command.eventId = UUID.fromString(stringifiedEventId);
    command.expenseId = UUID.fromString(stringifiedExpenseUuid);
    final ExecutionResult<Void> result = commandBus.sendAndWaitResponse(command);
    return getDataAsPayloadOrFail(result, HttpStatus.NO_CONTENT);
  }

  private final CommandBus commandBus;
}
