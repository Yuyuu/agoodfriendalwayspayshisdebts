package agoodfriendalwayspayshisdebts.model.participant;

import com.google.common.base.Objects;

import java.util.UUID;

public class Participant {

  private UUID id;
  private String name;
  private int share;
  private String email;

  /* Used by mongolink */
  @SuppressWarnings("unused")
  protected Participant() {}

  public Participant(String name, int share, String email) {
    id = UUID.randomUUID();
    this.name = name;
    this.share = share;
    this.email = email;
  }

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public int share() {
    return share;
  }

  public String email() {
    return email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Participant that = (Participant) o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
