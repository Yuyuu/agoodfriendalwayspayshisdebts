package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.vter.command.CommandHandler;

public class DeleteExpenseCommandHandler implements CommandHandler<DeleteExpenseCommand, Expense> {

  @Override
  public Expense execute(DeleteExpenseCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    return event.deleteExpense(command.expenseId);
  }
}
