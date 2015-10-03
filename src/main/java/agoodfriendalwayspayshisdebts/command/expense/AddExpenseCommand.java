package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.command.expense.validation.PositiveNonzeroAmount;
import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpenseDetails;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class AddExpenseCommand implements Command<ExpenseDetails> {
  public UUID eventId;

  @NotBlank(message = "EXPENSE_LABEL_REQUIRED")
  public String label;

  @NotBlank(message = "EXPENSE_PURCHASER_REQUIRED")
  public String purchaserUuid;

  @NotNull(message = "EXPENSE_AMOUNT_REQUIRED")
  @PositiveNonzeroAmount(message = "INVALID_AMOUNT")
  public Double amount;

  public List<String> participantsUuids;

  public String description;
}
