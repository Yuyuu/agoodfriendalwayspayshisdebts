package agoodfriendalwayspayshisdebts.command.event;

import com.vter.command.Command;
import org.hibernate.validator.constraints.Email;

import java.util.UUID;

public class UpdateParticipantCommand implements Command<Void> {
  public UUID eventId;

  public UUID id;

  @Email(message = "INVALID_EMAIL")
  public String email;
}
