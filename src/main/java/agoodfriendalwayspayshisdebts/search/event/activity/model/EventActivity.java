package agoodfriendalwayspayshisdebts.search.event.activity.model;

import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;
import java.util.UUID;

public class EventActivity {

  @MongoId
  public UUID id;
  public List<EventOperation> operations = Lists.newArrayList();

  private EventActivity() {}
}
