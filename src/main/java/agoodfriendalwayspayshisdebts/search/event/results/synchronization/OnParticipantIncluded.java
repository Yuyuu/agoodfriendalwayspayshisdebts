package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.operation.IncludeParticipantOperation;
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
    final EventResults result = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(EventResults.class);
    assert result != null;

    result.apply(new IncludeParticipantOperation(internalEvent.expense, internalEvent.participant));

    jongo.getCollection("eventresults_view").update("{_id:#}", result.eventId).with(result);
  }

  private final Jongo jongo;
}
