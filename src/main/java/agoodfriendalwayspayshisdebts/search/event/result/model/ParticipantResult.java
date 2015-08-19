package agoodfriendalwayspayshisdebts.search.event.result.model;

import agoodfriendalwayspayshisdebts.search.event.result.calculation.ParticipantDebtsDetails;

public class ParticipantResult {
  public double totalSpent;
  public ParticipantDebtsDetails participantDebtsDetails;

  public ParticipantResult(double totalSpent, ParticipantDebtsDetails participantDebtsDetails) {
    this.totalSpent = totalSpent;
    this.participantDebtsDetails = participantDebtsDetails;
  }
}
