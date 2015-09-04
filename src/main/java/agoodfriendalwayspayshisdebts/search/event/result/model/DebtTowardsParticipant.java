package agoodfriendalwayspayshisdebts.search.event.result.model;

public class DebtTowardsParticipant {
  private static double INITIAL_DEBT = 0D;

  public String creditorName;
  public double rawAmount;
  public double mitigatedAmount;

  @SuppressWarnings("unused")
  private DebtTowardsParticipant() {}

  public DebtTowardsParticipant(String creditorName) {
    this.creditorName = creditorName;
    this.rawAmount = INITIAL_DEBT;
    this.mitigatedAmount = INITIAL_DEBT;
  }
}
