package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;

public class EventDetails {
  @MongoId
  public UUID id;
  public String name;
  public List<Participant> participants = Lists.newArrayList();

  private EventDetails() {}

  public static EventDetails fromEvent(Event event) {
    EventDetails eventDetails = new EventDetails();
    eventDetails.id = event.getId();
    eventDetails.name = event.name();
    eventDetails.participants = event.participants();
    return eventDetails;
  }
}
