package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.command.expense.DeleteExpenseCommand;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.web.actions.BaseAction;
import net.codestory.http.annotations.Delete;
import net.codestory.http.annotations.Resource;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class DeleteExpense extends BaseAction {

  @Inject
  public DeleteExpense(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Delete("/events/:stringifiedEventUuid/expenses/:stringifiedExpenseUuid")
  public Expense delete(String stringifiedEventId, String stringifiedExpenseUuid) {
    final DeleteExpenseCommand command = new DeleteExpenseCommand();
    command.eventId = UUID.fromString(stringifiedEventId);
    command.expenseId = UUID.fromString(stringifiedExpenseUuid);
    final ExecutionResult<Expense> result = commandBus.sendAndWaitResponse(command);
    return getDataOrFail(result);
  }

  private final CommandBus commandBus;
}
