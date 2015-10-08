package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.model.DebtTowardsParticipant;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class AddParticipantOperation extends ResultOperation {

  public AddParticipantOperation(Participant participant, Map<UUID, String> eventParticipantsNames) {
    this.participant = participant;
    this.eventParticipantsNames = eventParticipantsNames;
  }

  @Override
  protected void applyOperation() {
    participantsResults.values().forEach(includeParticipant());

    final ParticipantResult participantResult = ParticipantResult.forParticipant(participant, eventParticipantsNames);
    participantsResults.put(participant.id(), participantResult);
  }

  private Consumer<ParticipantResult> includeParticipant() {
    return participantResult ->
        participantResult.debtsDetail().put(participant.id(),new DebtTowardsParticipant(participant.name()));
  }

  private final Participant participant;
  private final Map<UUID, String> eventParticipantsNames;
}
