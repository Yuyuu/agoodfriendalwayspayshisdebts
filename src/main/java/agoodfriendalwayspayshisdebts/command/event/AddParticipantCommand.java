package agoodfriendalwayspayshisdebts.command.event;

import com.vter.command.Command;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class AddParticipantCommand implements Command<UUID> {
  public UUID eventId;

  @NotBlank(message = "PARTICIPANT_NAME_REQUIRED")
  public String name;

  @Email(message = "INVALID_EMAIL")
  public String email;

  @NotNull(message = "PARTICIPANT_SHARE_REQUIRED")
  @Min(value = 1, message = "INVALID_SHARE")
  public Integer share;

  public List<String> expensesUuids;
}
