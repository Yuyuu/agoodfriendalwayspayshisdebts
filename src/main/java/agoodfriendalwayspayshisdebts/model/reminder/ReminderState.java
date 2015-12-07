package agoodfriendalwayspayshisdebts.model.reminder;

import agoodfriendalwayspayshisdebts.model.activity.OperationType;

public enum ReminderState {
  DELIVERED(OperationType.REMINDER_DELIVERED),
  DROPPED(OperationType.REMINDER_DROPPED);

  private OperationType operationType;

  ReminderState(OperationType operationType) {
    this.operationType = operationType;
  }

  public OperationType operationType() {
    return operationType;
  }

  public static ReminderState parseFromStringInsensitive(String state) {
    return ReminderState.valueOf(state.toUpperCase());
  }
}
