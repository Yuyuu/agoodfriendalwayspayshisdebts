package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.web.fluent.status.resolver.ExceptionResolver;
import net.codestory.http.constants.HttpStatus;

public class UnknownExpenseResolver implements ExceptionResolver<UnknownExpense> {

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }
}
