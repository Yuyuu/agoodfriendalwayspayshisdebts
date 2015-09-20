package agoodfriendalwayspayshisdebts.infrastructure.services;

public class RecipientReport {
  public String recipientName;
  public boolean success;

  private RecipientReport(String recipientName) {
    this.recipientName = recipientName;
    this.success = true;
  }

  public static RecipientReport success(String recipientName) {
    return new RecipientReport(recipientName);
  }
}
