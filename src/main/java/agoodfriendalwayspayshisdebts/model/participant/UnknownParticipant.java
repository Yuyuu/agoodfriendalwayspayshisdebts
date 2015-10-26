package agoodfriendalwayspayshisdebts.model.participant;

import com.vter.model.BusinessError;

public class UnknownParticipant extends BusinessError {

  public UnknownParticipant() {
    super("The event has no such participant");
  }
}
