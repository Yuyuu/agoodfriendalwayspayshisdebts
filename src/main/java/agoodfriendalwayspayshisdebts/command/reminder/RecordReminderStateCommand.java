package agoodfriendalwayspayshisdebts.command.reminder;

import com.vter.command.Command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

public class RecordReminderStateCommand implements Command<Void> {

  @Pattern(regexp = "delivered|dropped", flags = Pattern.Flag.CASE_INSENSITIVE)
  public String event;

  @NotNull
  public UUID eventId;

  @NotNull
  public UUID participantId;
}
