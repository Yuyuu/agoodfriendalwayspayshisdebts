package agoodfriendalwayspayshisdebts.command.email;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public class SendReminderCommand implements Command<Iterable<RecipientReport>> {
  public UUID eventId;

  public String locale;

  @NotEmpty
  public List<String> recipientsUuids;

  @NotBlank
  public String eventLink;
}
