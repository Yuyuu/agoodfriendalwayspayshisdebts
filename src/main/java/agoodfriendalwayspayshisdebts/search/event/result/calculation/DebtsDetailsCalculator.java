package agoodfriendalwayspayshisdebts.search.event.result.calculation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DebtsDetailsCalculator {

  public DebtsDetailsCalculator(List<Expense> expenses, List<Participant> participants) {
    this.expenses = expenses;
    this.participantsIds = participants.stream().map(Participant::id).collect(Collectors.toList());
    participants.forEach(participant -> {
      participantsShares.put(participant.id(), participant.share());
      debtsDetails.put(participant.id(), initializedParticipantDebtsDetails());
    });
  }

  public ParticipantDebtsDetails detailsOf(UUID participantId) {
    assert debtsDetails.containsKey(participantId);
    return debtsDetails.get(participantId);
  }

  public Map<UUID, ParticipantDebtsDetails> calculate() {
    for (Expense expense : expenses) {
      int expenseNumberOfShares = numberOfShares(expense);
      List<UUID> expenseParticipantsIdsWithoutPurchaser = participantsIdsWithoutPurchaser(expense);

      expenseParticipantsIdsWithoutPurchaser.forEach(allocateParticipantShare(expense, expenseNumberOfShares));
    }

    mitigateDebtsBetweenParticipants();

    return debtsDetails;
  }

  private ParticipantDebtsDetails initializedParticipantDebtsDetails() {
    return ParticipantDebtsDetails.initForIds(participantsIds);
  }

  private int numberOfShares(Expense expense) {
    return expense.participantsIds().stream().mapToInt(participantsShares::get).sum();
  }

  private static List<UUID> participantsIdsWithoutPurchaser(Expense expense) {
    return expense.participantsIds().stream()
        .filter(id -> !id.equals(expense.purchaserId()))
        .collect(Collectors.toList());
  }

  private Consumer<UUID> allocateParticipantShare(Expense expense, int expenseNumberOfShares) {
    return participantId -> {
      double amountAfterSplit = expense.amount() / expenseNumberOfShares * participantsShares.get(participantId);
      debtsDetails.get(participantId).increaseDebtTowards(expense.purchaserId(), amountAfterSplit);
    };
  }

  private void mitigateDebtsBetweenParticipants() {
    List<ParticipantIdPair> participantIdPairs = ParticipantIdPair.calculateAllFromList(participantsIds);
    participantIdPairs.forEach(pair -> {
      UUID idA = pair.firstId;
      UUID idB = pair.secondId;
      double aDebtTowardsB = debtsDetails.get(idA).debtTowards(idB);
      double bDebtTowardsA = debtsDetails.get(idB).debtTowards(idA);

      double mitigatedDebt = Math.abs(aDebtTowardsB - bDebtTowardsA);

      if (aDebtTowardsB > bDebtTowardsA) {
        detailsOf(idA).setFinalDebtTowards(idB, mitigatedDebt);
        detailsOf(idB).setFinalDebtTowards(idA, NO_DEBT);
      } else if (aDebtTowardsB < bDebtTowardsA) {
        detailsOf(idA).setFinalDebtTowards(idB, NO_DEBT);
        detailsOf(idB).setFinalDebtTowards(idA, mitigatedDebt);
      } else {
        detailsOf(idA).setFinalDebtTowards(idB, NO_DEBT);
        detailsOf(idB).setFinalDebtTowards(idA, NO_DEBT);
      }
    });
  }

  private final List<Expense> expenses;
  private final List<UUID> participantsIds;
  private final Map<UUID, Integer> participantsShares = Maps.newHashMap();
  private final Map<UUID, ParticipantDebtsDetails> debtsDetails = Maps.newHashMap();

  private static final double NO_DEBT = 0D;
}
