package agoodfriendalwayspayshisdebts.search.expense.synchronization;

import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnParticipantIncluded implements InternalEventHandler<ParticipantIncludedInternalEvent> {

  @Inject
  public OnParticipantIncluded(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(ParticipantIncludedInternalEvent internalEvent) {
    jongo.getCollection("expensesdetails_view")
        .update("{_id:#}", internalEvent.expense.getId())
        .with("{$push:{participantsNames:#}}", internalEvent.participant.name());
  }

  private final Jongo jongo;
}
