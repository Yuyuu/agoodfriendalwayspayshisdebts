package agoodfriendalwayspayshisdebts.search.expense.details.synchronization;

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
    jongo.getCollection("eventexpensesdetails_view")
        .update("{_id:#, 'expenses.id':#}", internalEvent.expense.eventId(), internalEvent.expense.id())
        .with("{$push:{expenses.$.participantsNames:#}}", internalEvent.participant.name());
  }

  private final Jongo jongo;
}
