package agoodfriendalwayspayshisdebts.model.expense;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Expense {

  private UUID id;
  private String label;
  private UUID purchaserId;
  private double amount;
  private Set<UUID> participantsIds = Sets.newHashSet();
  private String description;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Expense() {}

  public Expense(String label, UUID purchaserId, double amount, List<UUID> participantsIds) {
    id = UUID.randomUUID();
    this.label = label;
    this.purchaserId = purchaserId;
    this.amount = amount;
    this.participantsIds.addAll(participantsIds);
  }

  public double amount() {
    return amount;
  }

  public String description() {
    return description;
  }

  public UUID id() {
    return id;
  }

  public String label() {
    return label;
  }

  public Set<UUID> participantsIds() {
    return participantsIds;
  }

  public UUID purchaserId() {
    return purchaserId;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
