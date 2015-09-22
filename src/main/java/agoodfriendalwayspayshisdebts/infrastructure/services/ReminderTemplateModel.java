package agoodfriendalwayspayshisdebts.infrastructure.services;

import agoodfriendalwayspayshisdebts.search.event.result.model.DebtTowardsParticipant;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReminderTemplateModel {
  private static final double MINIMUM_DEBT_AMOUNT = .005;
  private static final Currency euro = Currency.getInstance("EUR");

  private String participantName;
  private String totalSpent;
  private boolean hasDebts;
  private String totalDebt;
  private List<Map<String, Object>> debts = Lists.newArrayList();

  public ReminderTemplateModel(ParticipantResult participantResult, Locale locale) {
    final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    format.setCurrency(euro);
    participantName = participantResult.participantName();
    totalSpent = format.format(participantResult.totalSpent());
    hasDebts = participantResult.totalDebt() >= MINIMUM_DEBT_AMOUNT;
    totalDebt = format.format(participantResult.totalDebt());
    participantResult.debtsDetail().values().stream().filter(isNotZero()).forEach(populateModelWithFormat(format));
  }

  private Consumer<DebtTowardsParticipant> populateModelWithFormat(NumberFormat format) {
    return debt -> {
      Map<String, Object> map = Maps.newHashMap();
      map.put("mitigatedAmount", format.format(debt.mitigatedAmount));
      map.put("creditorName", debt.creditorName);
      debts.add(map);
    };
  }

  private static Predicate<DebtTowardsParticipant> isNotZero() {
    return debt -> debt.mitigatedAmount >= MINIMUM_DEBT_AMOUNT;
  }

  @SuppressWarnings("unused")
  public List<Map<String, Object>> getDebts() {
    return debts;
  }

  @SuppressWarnings("unused")
  public String getParticipantName() {
    return participantName;
  }

  @SuppressWarnings("unused")
  public String getTotalDebt() {
    return totalDebt;
  }

  @SuppressWarnings("unused")
  public String getTotalSpent() {
    return totalSpent;
  }

  @SuppressWarnings("unused")
  public boolean isHasDebts() {
    return hasDebts;
  }
}
