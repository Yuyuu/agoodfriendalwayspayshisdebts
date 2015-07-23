import unittest

import serializers
import events


class ParticipantSerializerTestCase(unittest.TestCase):
    def test_a_participant_is_properly_serialized(self):
        participant = events.Participant('Bob', 1, 'bob@email.com')

        expected_result = {'name': 'Bob', 'share': 1, 'email': 'bob@email.com'}

        self.assertDictEqual(expected_result, serializers.ParticipantSerializer.serialize(participant))


class PurchaseSerializerTestCase(unittest.TestCase):
    def test_a_purchase_is_properly_serialized(self):
        purchase = events.Purchase('Bob', 'Errands', 14)
        purchase.description = 'hello'
        purchase.add_participant('Kim')

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
        self.maxDiff = None
        event = events.Event('Cool event', [events.Participant('Bob', 1)])
        event.add_purchase(events.Purchase('Bob', 'Errands', 14))

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
