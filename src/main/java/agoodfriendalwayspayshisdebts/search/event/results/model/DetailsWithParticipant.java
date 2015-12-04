package agoodfriendalwayspayshisdebts.search.event.results.model;

public class DetailsWithParticipant {

  public String participantName;
  public double rawDebt;
  public double mitigatedDebt;
  public double advance;

  @SuppressWarnings("unused")
  private DetailsWithParticipant() {}

  public DetailsWithParticipant(String participantName) {
    this.participantName = participantName;
  }
}
