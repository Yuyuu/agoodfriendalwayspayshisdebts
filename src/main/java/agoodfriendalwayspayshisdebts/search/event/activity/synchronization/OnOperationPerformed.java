package agoodfriendalwayspayshisdebts.search.event.activity.synchronization;

import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.activity.model.OperationDetails;
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
    final OperationDetails operationDetails = OperationDetails.forOperationPerformedInternalEvent(internalEvent);
    jongo.getCollection("operationdetails_view").insert(operationDetails);
  }

  private final Jongo jongo;
}
