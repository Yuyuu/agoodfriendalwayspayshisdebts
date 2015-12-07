package agoodfriendalwayspayshisdebts.model.reminder

import agoodfriendalwayspayshisdebts.model.activity.OperationType
import spock.lang.Specification

class ReminderStateTest extends Specification {

  def "is paired with an operation type"(ReminderState state, OperationType operationType) {
    expect:
    state.operationType() == operationType

    where:
    state                   || operationType
    ReminderState.DELIVERED || OperationType.REMINDER_DELIVERED
    ReminderState.DROPPED   || OperationType.REMINDER_DROPPED
  }

  def "can parse a reminder state from a string with any case"() {
    expect:
    ReminderState.parseFromStringInsensitive(stateAsString) == state

    where:
    stateAsString || state
    "dropped"     || ReminderState.DROPPED
    "DELIVERED"   || ReminderState.DELIVERED
  }
}
