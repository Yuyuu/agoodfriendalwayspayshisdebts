package agoodfriendalwayspayshisdebts.search.event.result.calculation;

import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResultCalculator {

  public ResultCalculator(Event event) {
    this.event = event;
    this.amountSpentCalculator = new AmountSpentCalculator(event.expenses());
    this.debtsDetailsCalculator = new DebtsDetailsCalculator(event.expenses(), event.participants());
  }

  public CalculationResult calculate() {
    Map<UUID, Double> amountSpentResult = amountSpentCalculator.calculate();
    Map<UUID, ParticipantDebtsDetails> debtsResult = debtsDetailsCalculator.calculate();

    Map<UUID, ParticipantResult> participantResults = event.participants().stream()
        .map(Participant::id)
        .collect(Collectors.toMap(
            id -> id,
            id -> {
              double amountSpent = Optional.ofNullable(amountSpentResult.get(id)).orElse(AMOUNT_SPENT_ZERO);
              ParticipantDebtsDetails debts = debtsResult.get(id);
              return new ParticipantResult(amountSpent, debts);
            }
        ));

    return new CalculationResult(event.getId(), participantResults);
  }

  void setAmountSpentCalculator(AmountSpentCalculator amountSpentCalculator) {
    this.amountSpentCalculator = amountSpentCalculator;
  }

  void setDebtsDetailsCalculator(DebtsDetailsCalculator debtsDetailsCalculator) {
    this.debtsDetailsCalculator = debtsDetailsCalculator;
  }

  private final Event event;

  private AmountSpentCalculator amountSpentCalculator;
  private DebtsDetailsCalculator debtsDetailsCalculator;

  private static final double AMOUNT_SPENT_ZERO = 0D;
}
