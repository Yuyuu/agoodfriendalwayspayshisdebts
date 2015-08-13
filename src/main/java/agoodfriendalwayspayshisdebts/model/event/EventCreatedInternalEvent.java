package agoodfriendalwayspayshisdebts.model.event;

import com.vter.model.internal_event.InternalEvent;
import com.vter.model.internal_event.Synchronous;

import java.util.UUID;

@Synchronous
public class EventCreatedInternalEvent implements InternalEvent {

  public EventCreatedInternalEvent(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
