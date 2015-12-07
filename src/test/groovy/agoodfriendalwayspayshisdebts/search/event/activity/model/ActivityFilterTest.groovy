package agoodfriendalwayspayshisdebts.search.event.activity.model

import agoodfriendalwayspayshisdebts.model.activity.OperationType
import spock.lang.Specification

class ActivityFilterTest extends Specification {

  def "can give the proper activity filter based on a parsed string"() {
    expect:
    ActivityFilter.parseFromString(filter) == expectedActivityFilter

    where:
    filter         || expectedActivityFilter
    "expenses"     || ActivityFilter.EXPENSES
    "participants" || ActivityFilter.PARTICIPANTS
    "reminders"    || ActivityFilter.REMINDERS
    "all"          || ActivityFilter.ALL
    ""             || ActivityFilter.ALL
  }

  def "associates an activity filter to a list of operation types"(
      ActivityFilter filter, int expectedCount, List<OperationType> expectedOperationTypes) {
    expect:
    filter.associatedOperationCount() == expectedCount
    filter.operationTypes() == expectedOperationTypes

    where:
    filter                      | expectedCount | expectedOperationTypes
    ActivityFilter.ALL          | 0             | []
    ActivityFilter.EXPENSES     | 2             | [OperationType.NEW_EXPENSE, OperationType.EXPENSE_DELETED]
    ActivityFilter.PARTICIPANTS | 2             | [OperationType.NEW_PARTICIPANT, OperationType.PARTICIPANT_EDITED]
    ActivityFilter.REMINDERS    | 2             | [OperationType.REMINDER_DELIVERED, OperationType.REMINDER_DROPPED]
  }
}
