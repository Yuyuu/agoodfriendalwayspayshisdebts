import unittest

from agoodfriendalwayspayshisdebts import calculation, events


class DebtsCalculatorTestCase(unittest.TestCase):
    def setUp(self):
        self.participant_a = events.Participant('A', 1)
        self.participant_bc = events.Participant('B & C', 2)
        self.participant_d = events.Participant('D', 1)
        self.participant_e = events.Participant('E', 1)
        self.participant_fgh = events.Participant('f, G & H', 3)

    def test_can_calculate_the_total_amount_spent_by_each_participant(self):
        participants = [self.participant_a, self.participant_bc, self.participant_d]
        event = events.Event(None, participants)
        all_participants = map(lambda participant: participant.id, participants)
        event.purchases = [
            events.Purchase(self.participant_a.id, 10.5, all_participants, None),
            events.Purchase(self.participant_bc.id, 15, all_participants, None),
            events.Purchase(self.participant_d.id, 20, all_participants, None),
            events.Purchase(self.participant_a.id, 25.34, all_participants, None),
            events.Purchase(self.participant_bc.id, 30, all_participants, None),
            events.Purchase(self.participant_a.id, 3.5, all_participants, None),
            events.Purchase(self.participant_a.id, 0.99, all_participants, None),
            events.Purchase(self.participant_bc.id, 13.37, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(40.33, result.of(self.participant_a.id).total_spent)
        self.assertAlmostEqual(58.37, result.of(self.participant_bc.id).total_spent)
        self.assertAlmostEqual(20, result.of(self.participant_d.id).total_spent)

    def test_can_calculate_the_total_debt_towards_each_participant(self):
        participants = [
            self.participant_a,
            self.participant_bc,
            self.participant_d,
            self.participant_e,
            self.participant_fgh,
        ]
        event = events.Event(None, participants)
        all_participants = map(lambda participant: participant.id, participants)
        event.purchases = [
            events.Purchase(self.participant_a.id, 10.5, all_participants, None),
            events.Purchase(self.participant_bc.id, 15, all_participants, None),
            events.Purchase(self.participant_d.id, 20.67, all_participants, None),
            events.Purchase(self.participant_e.id, 25.34, all_participants, None),
            events.Purchase(self.participant_fgh.id, 30, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(1.855, result.of(self.participant_a.id).get_debt_towards(self.participant_e.id))
        self.assertAlmostEqual(3.2925, result.of(self.participant_bc.id).get_debt_towards(self.participant_d.id))
        self.assertAlmostEqual(0.58375, result.of(self.participant_d.id).get_debt_towards(self.participant_e.id))
        self.assertAlmostEqual(0, result.of(self.participant_e.id).get_debt_towards(self.participant_a.id))
        self.assertAlmostEqual(4.00125, result.of(self.participant_fgh.id).get_debt_towards(self.participant_d.id))

    def test_can_calculate_the_total_debt_of_each_participant(self):
        participants = [
            self.participant_a,
            self.participant_bc,
            self.participant_d,
            self.participant_e,
            self.participant_fgh,
        ]
        event = events.Event(None, participants)
        all_participants = map(lambda participant: participant.id, participants)
        event.purchases = [
            events.Purchase(self.participant_a.id, 10.5, all_participants, None),
            events.Purchase(self.participant_bc.id, 15, all_participants, None),
            events.Purchase(self.participant_d.id, 20.67, all_participants, None),
            events.Purchase(self.participant_e.id, 25.34, all_participants, None),
            events.Purchase(self.participant_fgh.id, 30, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(3.12625, result.of(self.participant_a.id).total_debt)
        self.assertAlmostEqual(10.3775, result.of(self.participant_bc.id).total_debt)
        self.assertAlmostEqual(0.58375, result.of(self.participant_d.id).total_debt)
        self.assertAlmostEqual(0, result.of(self.participant_e.id).total_debt)
        self.assertAlmostEqual(9.94125, result.of(self.participant_fgh.id).total_debt)

    def test_purchases_with_different_participants_involved(self):
        participants = [
            self.participant_a,
            self.participant_bc,
            self.participant_d
        ]
        event = events.Event(None, participants)
        event.purchases = [
            events.Purchase(self.participant_a.id, 30, [self.participant_a.id, self.participant_bc.id], None),
            events.Purchase(self.participant_bc.id, 4.4, [
                self.participant_a.id,
                self.participant_bc.id,
                self.participant_d.id
            ], None),
            events.Purchase(self.participant_d.id, 12.88, [self.participant_a.id, self.participant_d.id], None),
            events.Purchase(self.participant_a.id, 6, [self.participant_a.id, self.participant_d.id], None),
            events.Purchase(self.participant_d.id, 1.20, [self.participant_bc.id, self.participant_d.id], None),
            events.Purchase(self.participant_bc.id, 15, [self.participant_bc.id, self.participant_a.id], None),
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(3.44, result.of(self.participant_a.id).total_debt)
        self.assertAlmostEqual(13.9, result.of(self.participant_bc.id).total_debt)
        self.assertAlmostEqual(0.3, result.of(self.participant_d.id).total_debt)


class CalculationResultTestCase(unittest.TestCase):
    def test_conversion_to_bson(self):
        result_123 = calculation.ParticipantResult()
        result_123.total_spent = 1
        result_123.total_debt = 1
        result_123.debts_detail = {'456': 1}

        result_456 = calculation.ParticipantResult()
        result_456.total_spent = 1
        result_456.total_debt = 1
        result_456.debts_detail = {'123': 1}
        result = calculation.CalculationResult('event_id', {'123': result_123, '456': result_456})

        expected_result = {'event_id': 'event_id', 'detail': {
            '123': {'total_spent': 1, 'total_debt': 1, 'debts_detail': {'456': 1}},
            '456': {'total_spent': 1, 'total_debt': 1, 'debts_detail': {'123': 1}}
        }}

        self.assertDictEqual(result.to_bson(), expected_result)
