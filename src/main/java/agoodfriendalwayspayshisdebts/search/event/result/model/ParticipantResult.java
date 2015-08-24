package agoodfriendalwayspayshisdebts.search.event.result.model;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParticipantResult {
  private static double INITIAL_DEBT = 0D;

  private double totalSpent;
  private double totalDebt;
  private Map<UUID, Double> debtsDetail = Maps.newHashMap();

  private ParticipantResult() {}

  public static ParticipantResult forParticipantId(UUID participantId, List<UUID> participantsIds) {
    final ParticipantResult participantResult = new ParticipantResult();
    participantsIds.stream()
        .filter(id -> !id.equals(participantId))
        .forEach(id -> participantResult.debtsDetail.put(id, INITIAL_DEBT));
    return participantResult;
  }

  public double totalSpent() {
    return totalSpent;
  }

  public double totalDebt() {
    return totalDebt;
  }

  public void increaseTotalAmountSpentBy(double amount) {
    totalSpent += amount;
  }

  public void updateDebtTowards(UUID creditorId, double amount) {
    totalDebt = totalDebt - debtsDetail.get(creditorId) + amount;
    debtsDetail.put(creditorId, amount);
  }

  public double debtTowards(UUID participantId) {
    return debtsDetail.get(participantId);
  }
}
