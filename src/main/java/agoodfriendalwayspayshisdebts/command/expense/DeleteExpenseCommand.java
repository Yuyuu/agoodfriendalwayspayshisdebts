package agoodfriendalwayspayshisdebts.command.expense;

import com.vter.command.Command;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class DeleteExpenseCommand implements Command<Void> {
  @NotNull
  public UUID eventId;

  @NotNull
  public UUID expenseId;
}
