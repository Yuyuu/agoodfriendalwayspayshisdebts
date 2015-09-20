package agoodfriendalwayspayshisdebts.command.email;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public class SendReminderCommand implements Command<Iterable<RecipientReport>> {
  public UUID eventId;

  @NotEmpty
  public List<String> recipientsUuids;
}
