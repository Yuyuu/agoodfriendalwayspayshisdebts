import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts import factories


class EventFactoryTestCase(unittest.TestCase):
    def test_can_create_an_event_from_a_document(self):
        event_uuid = uuid4()
        lea_id = uuid4()
        kim_id = uuid4()
        document = {
            'uuid': event_uuid,
            'name': 'Cool event',
            'participants': [
                {'id': lea_id, 'name': 'Lea', 'email': '', 'share': 1},
                {'id': kim_id, 'name': 'Kim', 'email': 'kim@email.com', 'share': 1},
            ],
            'purchases': [
                {
                    'purchaser_id': lea_id,
                    'label': 'Shopping',
                    'amount': 10,
                    'participants_ids': [lea_id],
                    'description': ''
                },
                {
                    'purchaser_id': kim_id,
                    'label': 'Cards',
                    'amount': 10,
                    'participants_ids': [lea_id],
                    'description': '10 cards at 1euro'
                }
            ]
        }

        event = factories.EventFactory.create_event_from_document(document)

        self.assertEqual(event_uuid, event.uuid)
        self.assertEqual('Cool event', event.name)
        self.assertEqual(lea_id, event.participants[0].id)
        self.assertEqual('Lea', event.participants[0].name)
        self.assertEqual('kim@email.com', event.participants[1].email)
        self.assertEqual(1, event.participants[1].share)
        self.assertEqual(lea_id, event.purchases[0].purchaser_id)
        self.assertEqual('Shopping', event.purchases[0].label)
        self.assertEqual(10, event.purchases[0].amount)
        self.assertListEqual([lea_id], event.purchases[1].participants_ids)
        self.assertEqual('10 cards at 1euro', event.purchases[1].description)
