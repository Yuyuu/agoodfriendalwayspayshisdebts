package agoodfriendalwayspayshisdebts.search.event.activity.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.search.event.activity.model.EventOperation;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnOperationPerformed implements InternalEventHandler<OperationPerformedInternalEvent> {

  @Inject
  public OnOperationPerformed(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(OperationPerformedInternalEvent internalEvent) {
    final EventOperation eventOperation = eventOperation(internalEvent);
    jongo.getCollection("eventactivity_view")
        .update("{_id:#}", internalEvent.eventId)
        .upsert()
        .with("{$push:{operations:#}}", eventOperation);
  }

  private static EventOperation eventOperation(OperationPerformedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    final Operation operation = event.operations().stream()
        .filter(it -> internalEvent.operationId.equals(it.id()))
        .findFirst()
        .orElseThrow(IllegalStateException::new);
    return EventOperation.forOperation(operation);
  }

  private final Jongo jongo;
}
