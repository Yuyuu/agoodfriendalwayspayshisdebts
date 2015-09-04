package agoodfriendalwayspayshisdebts.search.event.result.model;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class ParticipantResult {

  private String participantName;
  private int participantShare;
  private double totalSpent;
  private double totalDebt;
  private Map<UUID, DebtTowardsParticipant> debtsDetail = Maps.newHashMap();

  @SuppressWarnings("unused")
  private ParticipantResult() {}

  private ParticipantResult(String participantName, int participantShare) {
    this.participantName = participantName;
    this.participantShare = participantShare;
  }

  public static ParticipantResult forParticipant(Participant participant, Map<UUID, String> participantsNames) {
    final ParticipantResult participantResult = new ParticipantResult(participant.name(), participant.share());
    participantsNames.entrySet().stream()
        .filter(participantEntry -> !participantEntry.getKey().equals(participant.id()))
        .forEach(participantEntry -> participantResult.debtsDetail
            .put(participantEntry.getKey(), new DebtTowardsParticipant(participantEntry.getValue())));
    return participantResult;
  }

  public String participantName() {
    return participantName;
  }

  public int participantShare() {
    return participantShare;
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

  public void decreaseTotalAmountSpentBy(double amount) {
    totalSpent -= amount;
  }

  public void increaseRawDebtTowards(UUID creditorId, double amount) {
    debtsDetail.get(creditorId).rawAmount += amount;
  }

  public void decreaseRawDebtTowards(UUID creditorId, double amount) {
    debtsDetail.get(creditorId).rawAmount -= amount;
  }

  public void updateDebtTowards(UUID creditorId, double amount) {
    double currentDebtTowardsCreditor = debtsDetail.get(creditorId).mitigatedAmount;
    totalDebt = totalDebt - currentDebtTowardsCreditor + amount;
    debtsDetail.get(creditorId).mitigatedAmount = amount;
  }

  public double rawDebtTowards(UUID participantId) {
    return debtsDetail.get(participantId).rawAmount;
  }

  public double mitigatedDebtTowards(UUID participantId) {
    return debtsDetail.get(participantId).mitigatedAmount;
  }
}
