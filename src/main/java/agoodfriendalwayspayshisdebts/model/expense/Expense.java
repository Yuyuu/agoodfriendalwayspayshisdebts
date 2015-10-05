package agoodfriendalwayspayshisdebts.model.expense;

import com.google.common.base.Objects;
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
  private UUID eventId;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Expense() {}

  public Expense(String label, UUID purchaserId, double amount, List<UUID> participantsIds, UUID eventId) {
    id = UUID.randomUUID();
    this.label = label;
    this.purchaserId = purchaserId;
    this.amount = amount;
    this.participantsIds.addAll(participantsIds);
    this.eventId = eventId;
  }

  public double amount() {
    return amount;
  }

  public String description() {
    return description;
  }

  public UUID eventId() {
    return eventId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Expense expense = (Expense) o;
    return Objects.equal(id, expense.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
