package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.vter.command.Command;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class DeleteExpenseCommand implements Command<Expense> {
  @NotNull
  public UUID eventId;

  @NotNull
  public UUID expenseId;
}
