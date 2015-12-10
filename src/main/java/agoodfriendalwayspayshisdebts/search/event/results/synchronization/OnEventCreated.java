package agoodfriendalwayspayshisdebts.search.event.results.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.event.EventCreatedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import com.vter.model.internal_event.InternalEventHandler;
import org.jongo.Jongo;

import javax.inject.Inject;

public class OnEventCreated implements InternalEventHandler<EventCreatedInternalEvent> {

  @Inject
  public OnEventCreated(Jongo jongo) {
    this.jongo = jongo;
  }

  @Override
  public void executeInternalEvent(EventCreatedInternalEvent internalEvent) {
    final Event event = RepositoryLocator.events().get(internalEvent.eventId);
    assert event != null;
    final EventResults result = EventResults.forEvent(event);
    jongo.getCollection("eventresults_view").insert(result);
  }

  private final Jongo jongo;
}
