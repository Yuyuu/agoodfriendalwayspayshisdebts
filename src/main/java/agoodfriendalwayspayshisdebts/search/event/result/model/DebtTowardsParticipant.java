package agoodfriendalwayspayshisdebts.search.event.result.model;

public class DebtTowardsParticipant {
  public String creditorName;
  public double amount;

  @SuppressWarnings("unused")
  private DebtTowardsParticipant() {}

  public DebtTowardsParticipant(String creditorName, double amount) {
    this.creditorName = creditorName;
    this.amount = amount;
  }
}
