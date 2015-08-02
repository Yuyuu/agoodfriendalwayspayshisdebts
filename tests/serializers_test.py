import unittest

from agoodfriendalwayspayshisdebts import events, serializers, calculation


class ParticipantSerializerTestCase(unittest.TestCase):
    def test_a_participant_is_properly_serialized(self):
        participant = events.Participant('Bob', 1, 'bob@email.com')

        expected_result = {'id': str(participant.id), 'name': 'Bob', 'share': 1, 'email': 'bob@email.com'}

        self.assertDictEqual(expected_result, serializers.ParticipantSerializer.serialize(participant))


class PurchaseSerializerTestCase(unittest.TestCase):
    def test_a_purchase_is_properly_serialized(self):
        purchase = events.Purchase(123, 14, [123, 456], 'Errands')
        purchase.description = 'hello'

        expected_result = {
            'purchaserId': "123",
            'label': 'Errands',
            'amount': 14,
            'participantsIds': ["123", "456"],
            'description': 'hello'
        }

        self.assertDictEqual(expected_result, serializers.PurchaseSerializer.serialize(purchase))


class EventSerializerTestCase(unittest.TestCase):
    def test_an_event_is_properly_serialized(self):
        bob = events.Participant('Bob', 1)
        bob_id = str(bob.id)
        event = events.Event('Cool event', [bob])
        event.purchases.append(events.Purchase(bob.id, 14, [bob.id], 'Errands'))

        expected_result = {
            'id': str(event.uuid),
            'name': 'Cool event',
            'participants': [{'id': bob_id, 'name': 'Bob', 'share': 1, 'email': ''}],
            'purchases': [{
                'purchaserId': bob_id,
                'label': 'Errands',
                'amount': 14,
                'participantsIds': [bob_id],
                'description': None
            }]
        }

        self.assertDictEqual(expected_result, serializers.EventSerializer.serialize(event))


class CalculationResultSerializerTestCase(unittest.TestCase):
    def test_the_calculation_result_is_properly_serialized(self):
        debts_result = calculation.DebtsResultDetail()
        debts_result.add_result('123', {'total_spent': 3, 'total_debt': 1.3, 'debts_detail': {'456': 2.4}})
        debts_result.add_result('456', {'total_spent': 6, 'total_debt': 0, 'debts_detail': {'123': 0}})

        expected_result = {
            '123': {
                'totalSpent': 3,
                'totalDebt': 1.3,
                'debtsDetail': {'456': 2.4}
            },
            '456': {
                'totalSpent': 6,
                'totalDebt': 0,
                'debtsDetail': {'123': 0}
            },
        }

        self.assertDictEqual(expected_result, serializers.DebtsResultDetailSerializer.serialize(debts_result))
