import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts import factories


class EventFactoryTestCase(unittest.TestCase):
    def test_can_create_an_event_from_a_document(self):
        event_id = uuid4()
        lea_id = uuid4()
        kim_id = uuid4()
        document = {
            '_id': event_id,
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

        self.assertEqual(event_id, event.id)
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


class DebtsResultDetailFactoryTestCase(unittest.TestCase):
    def test_can_create_a_debts_result_detail_from_a_document(self):
        document = {'_id': 'an_id', 'event_id': 'id123', 'detail': {
            '123': {'total_spent': 1, 'total_debt': 1, 'debts_detail': {'456': 1}},
            '456': {'total_spent': 1, 'total_debt': 3, 'debts_detail': {'123': 1.6}}
        }}

        debts_result_detail = factories.ResultDetailFactory.create_from_document(document)

        self.assertEqual(2, len(debts_result_detail.participants_results))
        self.assertEqual('123', debts_result_detail.participants_results[0][0])
        self.assertEqual(3, debts_result_detail.participants_results[1][1]['total_debt'])
        self.assertEqual(1.6, debts_result_detail.participants_results[1][1]['debts_detail']['123'])
