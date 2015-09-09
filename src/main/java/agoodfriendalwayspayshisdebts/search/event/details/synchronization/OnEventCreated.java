package agoodfriendalwayspayshisdebts.search.event.details.synchronization;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.event.EventCreatedInternalEvent;
import agoodfriendalwayspayshisdebts.search.event.details.model.EventDetails;
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
    final EventDetails eventDetails = EventDetails.fromEvent(event);
    jongo.getCollection("eventdetails_view").insert(eventDetails);
  }

  private final Jongo jongo;
}
