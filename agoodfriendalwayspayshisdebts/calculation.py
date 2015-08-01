from __future__ import division
from itertools import combinations


INITIAL_DEBT = 0
NO_DEBT = 0


class CalculationResult:
    def __init__(self, event_id, results_per_participant):
        self.event_id = event_id
        self.results_per_participant = results_per_participant

    def of(self, participant_id):
        return self.results_per_participant[participant_id]

    def to_bson(self):
        return {
            'event_id': self.event_id,
            'detail': {
                str(participant_id): self.__participant_result_to_bson(participant_result)
                for participant_id, participant_result in self.results_per_participant.iteritems()
            }
        }

    @staticmethod
    def __participant_result_to_bson(participant_result):
        return {
            'total_spent': participant_result.total_spent,
            'total_debt': participant_result.total_debt,
            'debts_detail': {
                str(creditor_id): amount for creditor_id, amount in participant_result.debts_detail.iteritems()
            }
        }


class ParticipantResult:
    def __init__(self):
        self.total_spent = 0
        self.total_debt = INITIAL_DEBT
        self.debts_detail = {}

    def get_debt_towards(self, participant_id):
        return self.debts_detail[participant_id]


class DebtsCalculator:
    def __init__(self, event):
        self.event = event
        self.results = {}
        self.debts = {}
        self.shares = {}

        for participant in event.participants:
            self.shares[participant.id] = participant.share
            self.results[participant.id] = ParticipantResult()
            self.debts[participant.id] = {participant.id: INITIAL_DEBT for participant in self.event.participants}

    def calculate(self):
        for purchase in self.event.purchases:
            self.results[purchase.purchaser_id].total_spent += purchase.amount
            self.__split_purchase_between_participants(purchase)

        self.__mitigate_individual_debts()
        self.__calculate_mitigated_total_debt()

        return CalculationResult(self.event.uuid, self.results)

    def __split_purchase_between_participants(self, purchase):
        total_purchase_shares = self.__calculate_total_purchase_shares(purchase)
        participants_ids_without_purchaser = filter(
            (lambda _id: _id != purchase.purchaser_id),
            purchase.participants_ids
        )

        for participant_id in participants_ids_without_purchaser:
            debt_for_purchase_after_split = purchase.amount * self.shares[participant_id] / total_purchase_shares
            self.debts[participant_id][purchase.purchaser_id] += debt_for_purchase_after_split

    def __calculate_total_purchase_shares(self, purchase):
        return sum(map(lambda participant_id: self.shares[participant_id], purchase.participants_ids))

    def __mitigate_individual_debts(self):
        for (part_a, part_b) in self.__participant_2_tuple_combinations():
            a_debt_towards_b = self.debts[part_a.id][part_b.id]
            b_debt_towards_a = self.debts[part_b.id][part_a.id]

            if a_debt_towards_b > b_debt_towards_a:
                self.results[part_a.id].debts_detail[part_b.id] = a_debt_towards_b - b_debt_towards_a
                self.results[part_b.id].debts_detail[part_a.id] = NO_DEBT
            elif b_debt_towards_a > a_debt_towards_b:
                self.results[part_a.id].debts_detail[part_b.id] = NO_DEBT
                self.results[part_b.id].debts_detail[part_a.id] = b_debt_towards_a - a_debt_towards_b
            else:
                self.results[part_a.id].debts_detail[part_b.id] = NO_DEBT
                self.results[part_b.id].debts_detail[part_a.id] = NO_DEBT

    def __calculate_mitigated_total_debt(self):
        for participant in self.event.participants:
            self.results[participant.id].total_debt = sum(self.results[participant.id].debts_detail.values())

    def __participant_2_tuple_combinations(self):
        return combinations(self.event.participants, 2)
