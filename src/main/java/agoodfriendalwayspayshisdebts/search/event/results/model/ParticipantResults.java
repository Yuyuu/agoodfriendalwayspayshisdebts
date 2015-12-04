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
  private double totalAdvance;
  private Map<UUID, DetailsWithParticipant> details = Maps.newHashMap();

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
        .forEach(participantEntry -> participantResults.details
            .put(participantEntry.getKey(), new DetailsWithParticipant(participantEntry.getValue())));
    return participantResults;
  }

  public Map<UUID, DetailsWithParticipant> debtsDetails() {
    return details;
  }

  public String participantName() {
    return participantName;
  }

  public int participantShare() {
    return participantShare;
  }

  public double totalAdvance() {
    return totalAdvance;
  }

  public double totalSpent() {
    return totalSpent;
  }

  public double totalDebt() {
    return totalDebt;
  }

  public double rawDebtTowards(UUID participantId) {
    return details.get(participantId).rawDebt;
  }

  public double mitigatedDebtTowards(UUID participantId) {
    return details.get(participantId).mitigatedDebt;
  }

  public double advanceTowards(UUID participantId) {
    return details.get(participantId).advance;
  }

  public void increaseTotalAmountSpentBy(double amount) {
    totalSpent += amount;
  }

  public void decreaseTotalAmountSpentBy(double amount) {
    totalSpent -= amount;
  }

  public void increaseRawDebtTowards(UUID creditorId, double amount) {
    details.get(creditorId).rawDebt += amount;
  }

  public void decreaseRawDebtTowards(UUID creditorId, double amount) {
    details.get(creditorId).rawDebt -= amount;
  }

  public void updateDebtTowards(UUID creditorId, double amount) {
    double currentDebtTowardsCreditor = details.get(creditorId).mitigatedDebt;
    double currentAdvanceTowardsCreditor = details.get(creditorId).advance;
    totalDebt = totalDebt - currentDebtTowardsCreditor + amount;
    totalAdvance -= currentAdvanceTowardsCreditor;
    details.get(creditorId).mitigatedDebt = amount;
    details.get(creditorId).advance = 0D;
  }

  public void updateAdvanceTowards(UUID debtorId, double amount) {
    double currentDebtTowardsDebtor = details.get(debtorId).mitigatedDebt;
    double currentAdvanceTowardsDebtor = details.get(debtorId).advance;
    totalDebt -= currentDebtTowardsDebtor;
    totalAdvance = totalAdvance - currentAdvanceTowardsDebtor + amount;
    details.get(debtorId).mitigatedDebt = 0D;
    details.get(debtorId).advance = amount;
  }

  public void reset(UUID participantId) {
    double currentDebtTowardsParticipant = details.get(participantId).mitigatedDebt;
    double currentAdvanceTowardsParticipant = details.get(participantId).advance;
    totalDebt -= currentDebtTowardsParticipant;
    totalAdvance -= currentAdvanceTowardsParticipant;
    details.get(participantId).mitigatedDebt = 0D;
    details.get(participantId).advance = 0D;
  }
}
