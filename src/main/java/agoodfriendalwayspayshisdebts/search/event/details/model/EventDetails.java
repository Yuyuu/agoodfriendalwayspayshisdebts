package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventDetails {
  @MongoId
  public UUID id;
  public String name;
  public List<ParticipantDetails> participants = Lists.newArrayList();

  public EventDetails() {}

  public static EventDetails fromEvent(Event event) {
    EventDetails eventDetails = new EventDetails();
    eventDetails.id = event.getId();
    eventDetails.name = event.name();
    eventDetails.participants.addAll(
        event.participants().stream().map(ParticipantDetails::fromParticipant).collect(Collectors.toList())
    );
    return eventDetails;
  }
}
