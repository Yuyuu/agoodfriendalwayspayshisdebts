package agoodfriendalwayspayshisdebts.search.event.activity.model;

import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import com.google.common.collect.ImmutableList;

import java.util.List;

public enum ActivityFilter {
  ALL,
  EXPENSES(OperationType.NEW_EXPENSE, OperationType.EXPENSE_DELETED),
  PARTICIPANTS(OperationType.NEW_PARTICIPANT, OperationType.PARTICIPANT_EDITED),
  REMINDERS(OperationType.REMINDER_DELIVERED, OperationType.REMINDER_DROPPED);

  private final int associatedOperationCount;
  private final List<OperationType> operationTypes;

  ActivityFilter(OperationType... operationTypes) {
    this.associatedOperationCount = operationTypes.length;
    this.operationTypes = ImmutableList.copyOf(operationTypes);
  }

  public static ActivityFilter parseFromString(String filter) {
    if (filter == null) {
      return ActivityFilter.ALL;
    }
    switch (filter.toLowerCase()) {
      case "expenses":
        return ActivityFilter.EXPENSES;
      case "participants":
        return ActivityFilter.PARTICIPANTS;
      case "reminders":
        return ActivityFilter.REMINDERS;
      default:
        return ActivityFilter.ALL;
    }
  }

  public int associatedOperationCount() {
    return associatedOperationCount;
  }

  public List<OperationType> operationTypes() {
    return operationTypes;
  }

  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}
