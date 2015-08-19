package agoodfriendalwayspayshisdebts.search.event.result.calculation;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParticipantDebtsDetails {

  private ParticipantDebtsDetails() {}

  public static ParticipantDebtsDetails initForIds(List<UUID> participantsIds) {
    ParticipantDebtsDetails participantDebtsDetails = new ParticipantDebtsDetails();
    participantDebtsDetails.details = Maps.newHashMapWithExpectedSize(participantsIds.size());

    participantsIds.forEach(id -> participantDebtsDetails.details.put(id, ZERO_INITIAL_DEBT));

    return participantDebtsDetails;
  }

  public void increaseDebtTowards(UUID creditorId, double amount) {
    Double currentDebt = details.get(creditorId);
    details.put(creditorId, currentDebt + amount);
  }

  public void setFinalDebtTowards(UUID creditorId, double amount) {
    details.put(creditorId, amount);
    totalDebt += amount;
  }

  public double debtTowards(UUID participantId) {
    return details.get(participantId);
  }

  public double debtTowards(Participant participant) {
    return debtTowards(participant.id());
  }

  public double totalDebt() {
    return totalDebt;
  }

  private double totalDebt;
  private Map<UUID, Double> details;

  private static final double ZERO_INITIAL_DEBT = 0D;
}
