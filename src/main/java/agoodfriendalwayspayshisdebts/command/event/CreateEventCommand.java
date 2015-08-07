package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public class CreateEventCommand implements Command<UUID> {

  @NotBlank
  public String name;

  @NotEmpty
  public List<Participant> participants;
}
