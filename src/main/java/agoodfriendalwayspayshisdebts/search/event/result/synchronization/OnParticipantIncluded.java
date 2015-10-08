package agoodfriendalwayspayshisdebts.search.event.result.synchronization;

import agoodfriendalwayspayshisdebts.model.participant.ParticipantIncludedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.operation.IncludeParticipantOperation;
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
    final CalculationResult result = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", internalEvent.expense.eventId())
        .as(CalculationResult.class);
    assert result != null;

    result.apply(new IncludeParticipantOperation(internalEvent.expense, internalEvent.participant));

    jongo.getCollection("eventresult_view").update("{_id:#}", result.eventId).with(result);
  }

  private final Jongo jongo;
}
