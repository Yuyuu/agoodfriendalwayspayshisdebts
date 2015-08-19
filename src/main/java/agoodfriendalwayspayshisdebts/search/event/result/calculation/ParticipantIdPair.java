package agoodfriendalwayspayshisdebts.search.event.result.calculation;

import com.google.common.base.Objects;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParticipantIdPair {

  public ParticipantIdPair(UUID firstId, UUID secondId) {
    this.firstId = firstId;
    this.secondId = secondId;
  }

  public static List<ParticipantIdPair> calculateAllFromList(List<UUID> uuids) {
    return pairsBetween(first(uuids), tail(uuids));
  }

  private static List<ParticipantIdPair> pairsBetween(UUID id, List<UUID> uuids) {
    List<ParticipantIdPair> pairs = uuids.stream()
        .map(pairId -> new ParticipantIdPair(id, pairId))
        .collect(Collectors.toList());

    if (hasMoreThanOneIdRemaining(uuids)) {
      pairs.addAll(pairsBetween(first(uuids), tail(uuids)));
    }

    return pairs;
  }

  private static UUID first(List<UUID> uuids) {
    return uuids.get(0);
  }

  private static List<UUID> tail(List<UUID> uuids) {
    return uuids.subList(1, uuids.size());
  }

  private static boolean hasMoreThanOneIdRemaining(List<UUID> uuids) {
    return uuids.size() > 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParticipantIdPair that = (ParticipantIdPair) o;
    return (Objects.equal(firstId, that.firstId) || Objects.equal(firstId, that.secondId)) &&
        (Objects.equal(secondId, that.secondId) || Objects.equal(secondId, that.firstId));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(firstId, secondId);
  }

  public final UUID firstId;
  public final UUID secondId;
}
