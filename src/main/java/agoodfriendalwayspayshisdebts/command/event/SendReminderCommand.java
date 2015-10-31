package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.UUID;

public class SendReminderCommand implements Command<Iterable<RecipientReport>> {
  public UUID eventId;

  public String locale;

  @NotEmpty
  public List<String> recipientsUuids;

  @NotBlank
  @URL
  public String eventLink;
}
