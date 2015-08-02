import unittest

from agoodfriendalwayspayshisdebts import events
from rules import WithEventBus
from agoodfriendalwayspayshisdebts.internal_events import PurchaseAddedEvent


class ParticipantTestCase(unittest.TestCase):
    def test_generates_an_uuid_upon_creation_if_none_is_provided(self):
        participant = events.Participant('Bob & Lea', 2, 'bob@email.com')
        self.assertIsNotNone(participant.id)

    def test_conversion_to_bson(self):
        participant = events.Participant('Bob & Lea', 2, 'bob@email.com')
        expected_participant = {
            'id': participant.id,
            'name': 'Bob & Lea',
            'email': 'bob@email.com',
            'share': 2
        }

        self.assertDictEqual(participant.to_bson(), expected_participant)


class PurchaseTestCase(unittest.TestCase):
    def test_has_purchaser(self):
        purchaser_id = 123
        purchase = events.Purchase(purchaser_id, 1, [], '')
        self.assertEqual(purchaser_id, purchase.purchaser_id)
        
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
        self.assertEqual(len(purchase.participants_ids), 0)

    def test_has_no_description_by_default(self):
        purchase = events.Purchase('', 1, [], '')
        self.assertIsNone(purchase.description)

    def test_can_add_a_participant(self):
        purchase = events.Purchase('', 1, [123], '')
        purchase.add_participant(456)
        self.assertListEqual(purchase.participants_ids, [123, 456])

    def test_conversion_to_bson(self):
        purchase = events.Purchase(123, 10.04, [456], 'Shopping')
        expected_purchase = {
            'purchaser_id': 123,
            'label': 'Shopping',
            'amount': 10.04,
            'participants_ids': [456],
            'description': None
        }
        self.assertDictEqual(expected_purchase, purchase.to_bson())


class EventTestCase(unittest.TestCase):
    with_event_bus = WithEventBus()

    def setUp(self):
        self.with_event_bus.before()

    def tearDown(self):
        self.with_event_bus.after()

    def test_generates_an_uuid_upon_creation_if_none_is_provided(self):
        event = events.Event('', [])
        self.assertIsNotNone(event.id)

    def test_has_name(self):
        name = 'Cool event'
        event = events.Event(name, [])
        self.assertEqual(event.name, name)

    def test_has_participants(self):
        participants = [{'name': 'Kim'}, {'name': 'Lea'}]
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
        purchase = events.Purchase(123, 2, [], 'Shopping')
        event = events.Event('', [])
        event.add_purchase(purchase)
        self.assertListEqual(event.purchases, [purchase])

    def test_an_event_is_emitted_when_a_purchase_is_added(self):
        event = events.Event('cool event', [events.Participant('Kim', 1, uuid='1')], uuid='id123')

        event.add_purchase(events.Purchase('1', 1, ['1'], 'label'))

        emitted_event = self.with_event_bus.bus().last_event_of_type(PurchaseAddedEvent)
        self.assertIsNotNone(emitted_event)
        self.assertEqual('id123', emitted_event.event_id)

    def test_conversion_to_bson(self):
        bob = events.Participant('Bob', 1)
        kim = events.Participant('Kim', 1)
        event = events.Event('Cool event', [bob, kim])
        event.purchases.append(events.Purchase(bob.id, 10, [bob.id], 'Gas'))
        expected_event = {
            '_id': event.id,
            'name': 'Cool event',
            'participants': [
                {'id': bob.id, 'name': 'Bob', 'email': '', 'share': 1},
                {'id': kim.id, 'name': 'Kim', 'email': '', 'share': 1}
            ],
            'purchases': [{
                'purchaser_id': bob.id,
                'label': 'Gas',
                'amount': 10,
                'participants_ids': [bob.id],
                'description': None
            }]
        }
        self.assertDictEqual(expected_event, event.to_bson())
