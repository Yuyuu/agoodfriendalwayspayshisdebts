package agoodfriendalwayspayshisdebts.command.expense;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import com.vter.command.CommandHandler;

public class DeleteExpenseCommandHandler implements CommandHandler<DeleteExpenseCommand, Void> {

  @Override
  public Void execute(DeleteExpenseCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    event.deleteExpense(command.expenseId);
    return null;
  }
}
