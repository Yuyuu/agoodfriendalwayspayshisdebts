import unittest

from rules import WithMongoMock
import agoodfriendalwayspayshisdebts.search_handlers as handlers
from agoodfriendalwayspayshisdebts.errors import EntityNotFoundError
from agoodfriendalwayspayshisdebts import searches


class EventDetailsSearchHandlerTestCase(unittest.TestCase):
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_mongomock.before()
        handlers.DB = self.with_mongomock.db

    def tearDown(self):
        self.with_mongomock.after()
        handlers.DB = None

    def test_can_return_an_event_and_its_properties(self):
        self.with_mongomock.collection('eventdetails_view').insert({
            '_id': 'id123', 'name': 'cool event', 'purchases': [],
            'participants': [{'id': '1', 'name': 'Kim', 'share': 1, 'email': ''}]
        })

        event = handlers.SearchEventDetailsHandler().execute(searches.EventDetailsSearch('id123'))

        self.assertEqual('id123', event.id)
        self.assertEqual('cool event', event.name)
        self.assertEqual('Kim', event.participants[0].name)
        self.assertListEqual([], event.purchases)

    def test_an_error_is_raised_if_the_event_does_not_exist(self):
        handler = handlers.SearchEventDetailsHandler()

        self.assertRaises(EntityNotFoundError, handler.execute, searches.EventDetailsSearch('hello'))


class EventDebtsResultSearchHandler(unittest.TestCase):
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_mongomock.before()
        handlers.DB = self.with_mongomock.db

    def tearDown(self):
        self.with_mongomock.after()
        handlers.DB = None

    def test_can_return_an_event_debts_result(self):
        self.with_mongomock.collection('eventresult_view').insert({
            'event_id': 'id123', 'detail': {
                '123': {'total_spent': 1, 'total_debt': 5.4, 'debts_detail': {'456': 5.4}},
                '456': {'total_spent': 5, 'total_debt': 0, 'debts_detail': {'456': 0}}
            }
        })

        debts_result = handlers.SearchEventDebtsResultHandler().execute(searches.EventDebtsResultSearch('id123'))

        self.assertEqual(2, len(debts_result.participants_results))
        self.assertEqual('123', debts_result.participants_results[0][0])
        self.assertEqual(5.4, debts_result.participants_results[0][1]['debts_detail']['456'])
        self.assertEqual(5, debts_result.participants_results[1][1]['total_spent'])

    def test_an_error_is_raised_if_the_event_does_not_exist(self):
        handler = handlers.SearchEventDetailsHandler()

        self.assertRaises(EntityNotFoundError, handler.execute, searches.EventDetailsSearch('hello'))
