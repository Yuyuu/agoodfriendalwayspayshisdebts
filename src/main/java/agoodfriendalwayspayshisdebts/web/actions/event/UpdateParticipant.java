package agoodfriendalwayspayshisdebts.web.actions.event;

import net.codestory.http.annotations.Put;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

@Resource
public class UpdateParticipant {

  @Put("/events/:stringifiedEventUuid/participants/:stringifiedParticipantUuid")
  public Payload update() {
    return Payload.ok();
  }
}
