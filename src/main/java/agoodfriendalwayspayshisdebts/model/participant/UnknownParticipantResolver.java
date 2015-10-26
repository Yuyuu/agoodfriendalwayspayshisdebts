package agoodfriendalwayspayshisdebts.model.participant;

import com.vter.web.fluent.status.resolver.ExceptionResolver;
import net.codestory.http.constants.HttpStatus;

public class UnknownParticipantResolver implements ExceptionResolver<UnknownParticipant> {

  @Override
  public int status() {
    return HttpStatus.NOT_FOUND;
  }
}
