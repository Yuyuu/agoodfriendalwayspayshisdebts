package agoodfriendalwayspayshisdebts.command.event;

import com.vter.command.Command;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

public class UpdateParticipantCommand implements Command<Void> {
  public UUID eventId;

  public UUID id;

  @NotBlank(message = "PARTICIPANT_NAME_REQUIRED")
  public String name;

  @Email(message = "INVALID_EMAIL")
  public String email;
}
