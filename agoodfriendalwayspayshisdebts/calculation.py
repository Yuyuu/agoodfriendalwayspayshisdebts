from __future__ import division
from itertools import combinations


INITIAL_DEBT = 0
NO_DEBT = 0


class CalculationResult:
    def __init__(self, results_per_participant):
        self.results_per_participant = results_per_participant

    def of(self, participant_id):
        return self.results_per_participant[participant_id]


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
            self.shares[participant.name] = participant.share
            self.results[participant.name] = ParticipantResult()
            self.debts[participant.name] = {participant.name: INITIAL_DEBT for participant in self.event.participants}

    def calculate(self):
        for purchase in self.event.purchases:
            self.results[purchase.purchaser].total_spent += purchase.amount
            self.__split_purchase_between_participants(purchase)

        self.__mitigate_individual_debts()
        self.__calculate_mitigated_total_debt()

        return CalculationResult(self.results)

    def __split_purchase_between_participants(self, purchase):
        total_purchase_shares = self.__calculate_total_purchase_shares(purchase)
        participants_names_without_purchaser = filter(lambda name: name != purchase.purchaser, purchase.participants)

        for participant_name in participants_names_without_purchaser:
            debt_for_purchase_after_split = purchase.amount * self.shares[participant_name] / total_purchase_shares
            self.debts[participant_name][purchase.purchaser] += debt_for_purchase_after_split

    def __calculate_total_purchase_shares(self, purchase):
        return sum(map(lambda participant_name: self.shares[participant_name], purchase.participants))

    def __mitigate_individual_debts(self):
        for (part_a, part_b) in self.__participant_2_tuple_combinations():
            a_debt_towards_b = self.debts[part_a.name][part_b.name]
            b_debt_towards_a = self.debts[part_b.name][part_a.name]

            if a_debt_towards_b > b_debt_towards_a:
                self.results[part_a.name].debts_detail[part_b.name] = a_debt_towards_b - b_debt_towards_a
                self.results[part_b.name].debts_detail[part_a.name] = NO_DEBT
            elif b_debt_towards_a > a_debt_towards_b:
                self.results[part_a.name].debts_detail[part_b.name] = NO_DEBT
                self.results[part_b.name].debts_detail[part_a.name] = b_debt_towards_a - a_debt_towards_b
            else:
                self.results[part_a.name].debts_detail[part_b.name] = NO_DEBT
                self.results[part_b.name].debts_detail[part_a.name] = NO_DEBT

    def __calculate_mitigated_total_debt(self):
        for participant in self.event.participants:
            self.results[participant.name].total_debt = sum(self.results[participant.name].debts_detail.values())

    def __participant_2_tuple_combinations(self):
        return combinations(self.event.participants, 2)
