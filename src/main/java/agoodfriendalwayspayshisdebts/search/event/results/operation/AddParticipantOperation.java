package agoodfriendalwayspayshisdebts.search.event.results.operation;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.results.model.DetailsWithParticipant;
import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class AddParticipantOperation extends ResultsOperation {

  public AddParticipantOperation(Participant participant, Map<UUID, String> eventParticipantsNames) {
    this.participant = participant;
    this.eventParticipantsNames = eventParticipantsNames;
  }

  @Override
  protected void applyOperation() {
    participantsResults.values().forEach(includeParticipant());

    final ParticipantResults participantResults = ParticipantResults.forParticipant(participant, eventParticipantsNames);
    participantsResults.put(participant.id(), participantResults);
  }

  private Consumer<ParticipantResults> includeParticipant() {
    return participantResult ->
        participantResult.debtsDetails().put(participant.id(),new DetailsWithParticipant(participant.name()));
  }

  private final Participant participant;
  private final Map<UUID, String> eventParticipantsNames;
}
