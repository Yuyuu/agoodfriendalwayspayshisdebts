package agoodfriendalwayspayshisdebts.infrastructure.services;

public class RecipientReport {

  private RecipientReport(String recipientName) {
    this.recipientName = recipientName;
    success = true;
    error = null;
  }

  private RecipientReport(String recipientName, Throwable error) {
    this.recipientName = recipientName;
    success = false;
    this.error = error.getMessage();
  }

  public static RecipientReport success(String recipientName) {
    return new RecipientReport(recipientName);
  }

  public static RecipientReport error(String recipientName, Throwable error) {
    return new RecipientReport(recipientName, error);
  }

  public boolean isSuccess() {
    return success;
  }

  private final String recipientName;
  private final boolean success;
  private final String error;
}
