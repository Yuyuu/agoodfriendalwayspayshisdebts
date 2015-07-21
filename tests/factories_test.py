import unittest

from bson.objectid import ObjectId

import factories


class EventFactoryTestCase(unittest.TestCase):
    def test_can_create_an_event_from_a_document(self):
        object_id = ObjectId()
        document = {
            '_id': object_id,
            'name': 'Cool event',
            'participants': [
                {'name': 'Lea', 'email': '', 'share': 1},
                {'name': 'Kim', 'email': 'kim@email.com', 'share': 1},
            ],
            'purchases': [
                {
                    'purchaser': 'Lea',
                    'label': 'Shopping',
                    'amount': 10,
                    'participants': [],
                    'description': ''
                },
                {
                    'purchaser': 'Kim',
                    'label': 'Cards',
                    'amount': 10,
                    'participants': ['Lea'],
                    'description': '10 cards at 1euro'
                }
            ]
        }

        event = factories.EventFactory.create_event_from_document(document)

        self.assertEqual(object_id, event.oid)
        self.assertEqual('Cool event', event.name)
        self.assertEqual('Lea', event.participants[0].name)
        self.assertEqual('kim@email.com', event.participants[1].email)
        self.assertEqual(1, event.participants[1].share)
        self.assertEqual('Lea', event.purchases[0].purchaser)
        self.assertEqual('Shopping', event.purchases[0].label)
        self.assertEqual(10, event.purchases[0].amount)
        self.assertListEqual(['Lea'], event.purchases[1].participants)
        self.assertEqual('10 cards at 1euro', event.purchases[1].description)
