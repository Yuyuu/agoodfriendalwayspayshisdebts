package agoodfriendalwayspayshisdebts.search.event.results.model;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class ParticipantResults {

  private String participantName;
  private int participantShare;
  private double totalSpent;
  private double totalDebt;
  private Map<UUID, DebtTowardsParticipant> debtsDetails = Maps.newHashMap();

  @SuppressWarnings("unused")
  private ParticipantResults() {}

  private ParticipantResults(String participantName, int participantShare) {
    this.participantName = participantName;
    this.participantShare = participantShare;
  }

  public static ParticipantResults forParticipant(Participant participant, Map<UUID, String> participantsNames) {
    final ParticipantResults participantResults = new ParticipantResults(participant.name(), participant.share());
    participantsNames.entrySet().stream()
        .filter(participantEntry -> !participantEntry.getKey().equals(participant.id()))
        .forEach(participantEntry -> participantResults.debtsDetails
            .put(participantEntry.getKey(), new DebtTowardsParticipant(participantEntry.getValue())));
    return participantResults;
  }

  public Map<UUID, DebtTowardsParticipant> debtsDetails() {
    return debtsDetails;
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
    debtsDetails.get(creditorId).rawAmount += amount;
  }

  public void decreaseRawDebtTowards(UUID creditorId, double amount) {
    debtsDetails.get(creditorId).rawAmount -= amount;
  }

  public void updateDebtTowards(UUID creditorId, double amount) {
    double currentDebtTowardsCreditor = debtsDetails.get(creditorId).mitigatedAmount;
    totalDebt = totalDebt - currentDebtTowardsCreditor + amount;
    debtsDetails.get(creditorId).mitigatedAmount = amount;
  }

  public double rawDebtTowards(UUID participantId) {
    return debtsDetails.get(participantId).rawAmount;
  }

  public double mitigatedDebtTowards(UUID participantId) {
    return debtsDetails.get(participantId).mitigatedAmount;
  }
}
