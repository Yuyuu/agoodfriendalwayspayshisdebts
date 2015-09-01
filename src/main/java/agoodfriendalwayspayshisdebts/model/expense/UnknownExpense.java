package agoodfriendalwayspayshisdebts.model.expense;

import com.vter.model.BusinessError;

public class UnknownExpense extends BusinessError {

  public UnknownExpense() {
    super("The event has no such expense");
  }
}
