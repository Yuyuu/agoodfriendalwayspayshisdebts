package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import com.google.common.collect.Sets;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventDetails {
  @MongoId
  public UUID id;
  public String name;
  public Set<ParticipantDetails> participants = Sets.newHashSet();

  private EventDetails() {}

  public static EventDetails fromEvent(Event event) {
    final EventDetails eventDetails = new EventDetails();
    eventDetails.id = event.getId();
    eventDetails.name = event.name();
    eventDetails.participants.addAll(
        event.participants().stream().map(ParticipantDetails::fromParticipant).collect(Collectors.toList())
    );
    return eventDetails;
  }
}
