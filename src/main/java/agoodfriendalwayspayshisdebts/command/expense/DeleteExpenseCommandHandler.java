package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.vter.command.CommandHandler;

public class DeleteExpenseCommandHandler implements CommandHandler<DeleteExpenseCommand, Void> {

  @Override
  public Void execute(DeleteExpenseCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final Expense expense = event.deleteExpense(command.expenseId);
    event.addOperation(new Operation(OperationType.EXPENSE_DELETED, expense.label(), event.getId()));
    return null;
  }
}
