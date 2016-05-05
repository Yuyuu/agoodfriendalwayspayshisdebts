package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.command.expense.AddExpenseCommand;
import agoodfriendalwayspayshisdebts.search.expense.model.ExpenseDetails;
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
public class AddExpense extends BaseAction {

  @Inject
  public AddExpense(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/expenses")
  public Payload add(String stringifiedUuid, AddExpenseCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);
    final ExecutionResult<ExpenseDetails> result = commandBus.sendAndWaitResponse(command);
    return getDataAsPayloadOrFail(result, HttpStatus.CREATED);
  }

  private final CommandBus commandBus;
}
