package agoodfriendalwayspayshisdebts.search.event.results.operation;

import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;
import com.google.common.collect.Lists;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ResultsOperation implements Consumer<EventResults> {

  @Override
  public void accept(EventResults result) {
    this.participantsResults = result.participantsResults;
    applyOperation();
  }

  protected abstract void applyOperation();

  protected final void mitigateDebtBetween(UUID aId, UUID bId) {
    final double aDebtTowardsB = participantsResults.get(aId).rawDebtTowards(bId);
    final double bDebtTowardsA = participantsResults.get(bId).rawDebtTowards(aId);
    final double mitigatedDebt = Math.abs(bDebtTowardsA - aDebtTowardsB);

    if (bDebtTowardsA > aDebtTowardsB) {
      participantsResults.get(bId).updateDebtTowards(aId, mitigatedDebt);
      participantsResults.get(aId).updateAdvanceTowards(bId, mitigatedDebt);
    } else if (bDebtTowardsA < aDebtTowardsB) {
      participantsResults.get(bId).updateAdvanceTowards(aId, mitigatedDebt);
      participantsResults.get(aId).updateDebtTowards(bId, mitigatedDebt);
    } else {
      participantsResults.get(bId).reset(aId);
      participantsResults.get(aId).reset(bId);
    }
  }

  protected static Predicate<UUID> hasNotId(UUID... ids) {
    return entityId -> !Lists.newArrayList(ids).contains(entityId);
  }

  protected Map<UUID, ParticipantResults> participantsResults;
}
