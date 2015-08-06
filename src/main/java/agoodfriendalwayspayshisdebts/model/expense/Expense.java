package agoodfriendalwayspayshisdebts.model.expense;

import java.util.List;
import java.util.UUID;

public class Expense {

  private String label;
  private UUID purchaserId;
  private double amount;
  private List<UUID> participantIds;
  private String description;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Expense() {}

  public Expense(String label, UUID purchaserId, double amount, List<UUID> participantsIds) {
    this.label = label;
    this.purchaserId = purchaserId;
    this.amount = amount;
    this.participantIds = participantsIds;
  }

  public double amount() {
    return amount;
  }

  public String description() {
    return description;
  }

  public String label() {
    return label;
  }

  public List<UUID> participantIds() {
    return participantIds;
  }

  public UUID purchaserId() {
    return purchaserId;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
