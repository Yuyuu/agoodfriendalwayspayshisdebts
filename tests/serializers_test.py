import unittest

from agoodfriendalwayspayshisdebts import events, serializers, calculation


class ParticipantSerializerTestCase(unittest.TestCase):
    def test_a_participant_is_properly_serialized(self):
        participant = events.Participant('Bob', 1, 'bob@email.com')

        expected_result = {'name': 'Bob', 'share': 1, 'email': 'bob@email.com'}

        self.assertDictEqual(expected_result, serializers.ParticipantSerializer.serialize(participant))


class PurchaseSerializerTestCase(unittest.TestCase):
    def test_a_purchase_is_properly_serialized(self):
        purchase = events.Purchase('Bob', 14, ['Kim'], 'Errands')
        purchase.description = 'hello'

        expected_result = {
            'purchaser': 'Bob',
            'label': 'Errands',
            'amount': 14,
            'participants': ['Kim'],
            'description': 'hello'
        }

        self.assertDictEqual(expected_result, serializers.PurchaseSerializer.serialize(purchase))


class EventSerializerTestCase(unittest.TestCase):
    def test_an_event_is_properly_serialized(self):
        event = events.Event('Cool event', [events.Participant('Bob', 1)])
        event.add_purchase(events.Purchase('Bob', 14, [], 'Errands'))

        expected_result = {
            'id': str(event.uuid),
            'name': 'Cool event',
            'participants': [{'name': 'Bob', 'share': 1, 'email': ''}],
            'purchases': [{
                'purchaser': 'Bob',
                'label': 'Errands',
                'amount': 14,
                'participants': [],
                'description': None
            }]
        }

        self.assertDictEqual(expected_result, serializers.EventSerializer.serialize(event))


class CalculationResultSerializerTestCase(unittest.TestCase):
    def test_the_calculation_result_is_properly_serialized(self):
        kim_result = calculation.ParticipantResult()
        kim_result.total_spent = 10
        kim_result.total_debt = 5
        kim_result.debts_detail = {'Joe': 0, 'Lea': 5}

        joe_result = calculation.ParticipantResult()
        joe_result.total_spent = 6
        joe_result.total_debt = 7
        joe_result.debts_detail = {'Kim': 2, 'Lea': 5}

        lea_result = calculation.ParticipantResult()
        lea_result.total_spent = 15
        lea_result.total_debt = 0
        lea_result.debts_detail = {'Kim': 0, 'Joe': 0}

        result = calculation.CalculationResult({
            'Kim': kim_result,
            'Joe': joe_result,
            'Lea': lea_result
        })

        expected_result = {
            'Kim': {
                'total_spent': 10,
                'total_debt': 5,
                'debts_detail': {'Joe': 0, 'Lea': 5}
            },
            'Joe': {
                'total_spent': 6,
                'total_debt': 7,
                'debts_detail': {'Kim': 2, 'Lea': 5}
            },
            'Lea': {
                'total_spent': 15,
                'total_debt': 0,
                'debts_detail': {'Kim': 0, 'Joe': 0}
            }
        }

        self.assertDictEqual(expected_result, serializers.CalculationResultSerializer.serialize(result))
