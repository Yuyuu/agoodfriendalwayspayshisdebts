package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.participant.Participant;

import java.util.UUID;

public class ParticipantDetails {
  public UUID id;
  public String name;
  public int share;
  public String email;
  public UUID eventId;

  public ParticipantDetails() {}

  public static ParticipantDetails forParticipant(Participant participant) {
    final ParticipantDetails participantDetails = new ParticipantDetails();
    participantDetails.id = participant.getId();
    participantDetails.name = participant.name();
    participantDetails.share = participant.share();
    participantDetails.email = participant.email();
    participantDetails.eventId = participant.eventId();
    return participantDetails;
  }
}
