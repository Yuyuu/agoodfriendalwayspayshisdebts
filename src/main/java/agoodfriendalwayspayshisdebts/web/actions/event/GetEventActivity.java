package agoodfriendalwayspayshisdebts.web.actions.event;

import com.google.common.collect.Lists;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Resource
public class GetEventActivity {

  @Get("/events/:stringifiedEventUuid/activity")
  public Optional<Iterable<Activity>> get(String stringifiedEventUuid) {
    final UUID eventId = UUID.fromString(stringifiedEventUuid);
    return Optional.of(Lists.newArrayList(
        new Activity(eventId, "EVENT_CREATION"),
        new Activity(eventId, "NEW_EXPENSE"),
        new Activity(eventId, "NEW_PARTICIPANT"),
        new Activity(eventId, "NEW_EXPENSE"),
        new Activity(eventId, "NEW_EXPENSE")
    ));
  }

  private static class Activity {
    public UUID id;
    public UUID eventId;
    public Date creationDate;
    public String type;

    public Activity(UUID eventId, String type) {
      this.id = UUID.randomUUID();
      this.eventId = eventId;
      this.creationDate = new Date();
      this.type = type;
    }
  }
}
