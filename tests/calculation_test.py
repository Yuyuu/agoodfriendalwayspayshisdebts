import unittest

from agoodfriendalwayspayshisdebts import calculation, events


class DebtsCalculatorTestCase(unittest.TestCase):
    def test_can_calculate_the_total_amount_spent_by_each_participant(self):
        participants = [
            events.Participant('A', 1),
            events.Participant('B & C', 2),
            events.Participant('D', 1)
        ]
        event = events.Event(None, participants)
        all_participants = ['A', 'B & C', 'D']
        event.purchases = [
            events.Purchase('A', 10.5, all_participants, None),
            events.Purchase('B & C', 15, all_participants, None),
            events.Purchase('D', 20, all_participants, None),
            events.Purchase('A', 25.34, all_participants, None),
            events.Purchase('B & C', 30, all_participants, None),
            events.Purchase('A', 3.5, all_participants, None),
            events.Purchase('A', 0.99, all_participants, None),
            events.Purchase('B & C', 13.37, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(40.33, result.of('A').total_spent)
        self.assertAlmostEqual(58.37, result.of('B & C').total_spent)
        self.assertAlmostEqual(20, result.of('D').total_spent)

    def test_can_calculate_the_total_debt_towards_each_participant(self):
        participants = [
            events.Participant('A', 1),
            events.Participant('B & C', 2),
            events.Participant('D', 1),
            events.Participant('E', 1),
            events.Participant('F, G & H', 3)
        ]
        event = events.Event(None, participants)
        all_participants = ['A', 'B & C', 'D', 'E', 'F, G & H']
        event.purchases = [
            events.Purchase('A', 10.5, all_participants, None),
            events.Purchase('B & C', 15, all_participants, None),
            events.Purchase('D', 20.67, all_participants, None),
            events.Purchase('E', 25.34, all_participants, None),
            events.Purchase('F, G & H', 30, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(1.855, result.of('A').get_debt_towards('E'))
        self.assertAlmostEqual(3.2925, result.of('B & C').get_debt_towards('D'))
        self.assertAlmostEqual(0.58375, result.of('D').get_debt_towards('E'))
        self.assertAlmostEqual(0, result.of('E').get_debt_towards('A'))
        self.assertAlmostEqual(4.00125, result.of('F, G & H').get_debt_towards('D'))

    def test_can_calculate_the_total_debt_of_each_participant(self):
        participants = [
            events.Participant('A', 1),
            events.Participant('B & C', 2),
            events.Participant('D', 1),
            events.Participant('E', 1),
            events.Participant('F, G & H', 3)
        ]
        event = events.Event(None, participants)
        all_participants = ['A', 'B & C', 'D', 'E', 'F, G & H']
        event.purchases = [
            events.Purchase('A', 10.5, all_participants, None),
            events.Purchase('B & C', 15, all_participants, None),
            events.Purchase('D', 20.67, all_participants, None),
            events.Purchase('E', 25.34, all_participants, None),
            events.Purchase('F, G & H', 30, all_participants, None)
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(3.12625, result.of('A').total_debt)
        self.assertAlmostEqual(10.3775, result.of('B & C').total_debt)
        self.assertAlmostEqual(0.58375, result.of('D').total_debt)
        self.assertAlmostEqual(0, result.of('E').total_debt)
        self.assertAlmostEqual(9.94125, result.of('F, G & H').total_debt)

    def test_purchases_with_different_participants_involved(self):
        participants = [
            events.Participant('A', 1),
            events.Participant('B & C', 2),
            events.Participant('D', 1)
        ]
        event = events.Event(None, participants)
        event.purchases = [
            events.Purchase('A', 30, ['A', 'B & C'], None),
            events.Purchase('B & C', 4.4, ['A', 'B & C', 'D'], None),
            events.Purchase('D', 12.88, ['A', 'D'], None),
            events.Purchase('A', 6, ['A', 'D'], None),
            events.Purchase('D', 1.20, ['B & C', 'D'], None),
            events.Purchase('B & C', 15, ['B & C', 'A'], None),
        ]

        result = calculation.DebtsCalculator(event).calculate()

        self.assertAlmostEqual(3.44, result.of('A').total_debt)
        self.assertAlmostEqual(13.9, result.of('B & C').total_debt)
        self.assertAlmostEqual(0.3, result.of('D').total_debt)
