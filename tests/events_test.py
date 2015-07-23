import unittest

import events


class ParticipantTestCase(unittest.TestCase):
    def test_conversion_to_bson(self):
        participant = events.Participant('Bob & Lea', 2, 'bob@email.com')
        expected_participant = {
            'name': 'Bob & Lea',
            'email': 'bob@email.com',
            'share': 2
        }

        self.assertDictEqual(participant.to_bson(), expected_participant)


class PurchaseTestCase(unittest.TestCase):
    def test_has_purchaser(self):
        purchaser = 'Kim'
        purchase = events.Purchase(purchaser, 1, [], '')
        self.assertEqual(purchaser, purchase.purchaser)
        
    def label(self):
        label = 'Shopping'
        purchase = events.Purchase('', 1, [], 'Shopping')
        self.assertEqual(label, purchase.label)
        
    def test_has_amount(self):
        amount = 10.04
        purchase = events.Purchase('', amount, [], '')
        self.assertEqual(amount, purchase.amount)
        
    def test_has_no_participants_by_default(self):
        purchase = events.Purchase('', 1, [], '')
        self.assertEqual(len(purchase.participants), 0)

    def test_has_no_description_by_default(self):
        purchase = events.Purchase('', 1, [], '')
        self.assertIsNone(purchase.description)

    def test_can_add_a_participant(self):
        purchase = events.Purchase('', 1, ['Bob'], '')
        self.assertListEqual(purchase.participants, ['Bob'])

    def test_conversion_to_bson(self):
        purchase = events.Purchase('Kim', 10.04, ['Bob'], 'Shopping')
        expected_purchase = {
            'purchaser': 'Kim',
            'label': 'Shopping',
            'amount': 10.04,
            'participants': ['Bob'],
            'description': None
        }
        self.assertDictEqual(expected_purchase, purchase.to_bson())


class EventTestCase(unittest.TestCase):
    def test_generates_an_uuid_upon_creation_if_none_is_provided(self):
        event = events.Event('', [])
        self.assertIsNotNone(event.uuid)

    def test_has_name(self):
        name = 'Cool event'
        event = events.Event(name, [])
        self.assertEqual(event.name, name)

    def test_has_participants(self):
        participants = ['Kim', 'Lea', 'Bob']
        event = events.Event('', participants)
        self.assertListEqual(event.participants, participants)

    def test_has_no_purchases_at_start(self):
        event = events.Event('', [])
        self.assertEqual(len(event.purchases), 0)

    def test_can_add_a_participant(self):
        participant = events.Participant('Kim', 1)
        event = events.Event('', [])
        event.add_participant(participant)
        self.assertListEqual(event.participants, [participant])

    def test_can_add_a_purchase(self):
        purchase = events.Purchase('Kim', [], 2, 'Shopping')
        event = events.Event('', [])
        event.add_purchase(purchase)
        self.assertListEqual(event.purchases, [purchase])

    def test_conversion_to_bson(self):
        event = events.Event('Cool event', [events.Participant('Bob', 1), events.Participant('Kim', 1)])
        purchase = events.Purchase('Bob', 10, [], 'Gas')
        event.add_purchase(purchase)
        expected_event = {
            'uuid': event.uuid,
            'name': 'Cool event',
            'participants': [{'name': 'Bob', 'email': '', 'share': 1}, {'name': 'Kim', 'email': '', 'share': 1}],
            'purchases': [{'purchaser': 'Bob', 'label': 'Gas', 'amount': 10, 'participants': [], 'description': None}]
        }
        self.assertDictEqual(expected_event, event.to_bson())
