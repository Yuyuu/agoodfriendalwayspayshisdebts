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
        kim = events.Participant('Kim', 1)
        kim_id_as_string = str(kim.id)
        joe = events.Participant('Joe', 1)
        joe_id_as_string = str(joe.id)
        lea = events.Participant('Lea', 1)
        lea_id_as_string = str(lea.id)

        kim_result = calculation.ParticipantResult()
        kim_result.total_spent = 10
        kim_result.total_debt = 5
        kim_result.debts_detail = {joe.id: 0, lea.id: 5}

        joe_result = calculation.ParticipantResult()
        joe_result.total_spent = 6
        joe_result.total_debt = 7
        joe_result.debts_detail = {kim.id: 2, lea.id: 5}

        lea_result = calculation.ParticipantResult()
        lea_result.total_spent = 15
        lea_result.total_debt = 0
        lea_result.debts_detail = {kim.id: 0, joe.id: 0}

        result = calculation.CalculationResult('id123', {kim.id: kim_result, joe.id: joe_result, lea.id: lea_result})

        expected_result = {
            kim_id_as_string: {
                'totalSpent': 10,
                'totalDebt': 5,
                'debtsDetail': {joe_id_as_string: 0, lea_id_as_string: 5}
            },
            joe_id_as_string: {
                'totalSpent': 6,
                'totalDebt': 7,
                'debtsDetail': {kim_id_as_string: 2, lea_id_as_string: 5}
            },
            lea_id_as_string: {
                'totalSpent': 15,
                'totalDebt': 0,
                'debtsDetail': {kim_id_as_string: 0, joe_id_as_string: 0}
            }
        }

        self.assertDictEqual(expected_result, serializers.CalculationResultSerializer.serialize(result))
