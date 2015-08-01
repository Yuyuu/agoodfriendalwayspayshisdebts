import unittest

from rules import WithMemoryRepository, WithMongoMock
import agoodfriendalwayspayshisdebts.event_search_handlers as handlers
from agoodfriendalwayspayshisdebts.events import Event, Participant, Purchase
from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
from agoodfriendalwayspayshisdebts.errors import EntityNotFoundError
from agoodfriendalwayspayshisdebts import internal_events, searches


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
            'uuid': 'id123', 'name': 'cool event', 'purchases': [],
            'participants': [{'id': '1', 'name': 'Kim', 'share': 1, 'email': ''}]
        })

        event = handlers.SearchEventDetailsHandler().execute(searches.EventDetailsSearch('id123'))

        self.assertEqual('id123', event.uuid)
        self.assertEqual('cool event', event.name)
        self.assertEqual('Kim', event.participants[0].name)
        self.assertListEqual([], event.purchases)

    def test_an_error_is_raised_if_the_event_does_not_exist(self):
        handler = handlers.SearchEventDetailsHandler()

        self.assertRaises(EntityNotFoundError, handler.execute, searches.EventDetailsSearch('hello'))


class OnEventCreatedTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_memory_repository.before()
        self.with_mongomock.before()
        handlers.DB = self.with_mongomock.db
        self.handler = handlers.OnEventCreated()

    def tearDown(self):
        self.with_memory_repository.after()
        self.with_mongomock.after()
        handlers.DB = None

    def test_can_create_the_event_details(self):
        event = Event('cool event', [Participant('Kim', 1, uuid='1')], 'id123')
        internal_event = internal_events.EventCreatedEvent(event.uuid)
        RepositoryLocator.events().add(event)

        self.handler.execute(internal_event)

        document = self.with_mongomock.collection('eventdetails_view').find_one()
        self.assertIsNotNone(document)
        self.assertEqual('id123', document['uuid'])
        self.assertEqual('cool event', document['name'])
        self.assertEqual('Kim', document['participants'][0]['name'])


class OnPurchaseAddedUpdateViewTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_memory_repository.before()
        self.with_mongomock.before()
        handlers.DB = self.with_mongomock.db
        self.handler = handlers.OnPurchaseAddedUpdateView()

    def tearDown(self):
        self.with_memory_repository.after()
        self.with_mongomock.after()
        handlers.DB = None

    def test_the_event_details_is_updated(self):
        self.with_mongomock.collection('eventdetails_view').insert({
            'uuid': 'id123', 'name': 'cool event', 'purchases': [],
            'participants': [{'id': '1', 'name': 'Kim', 'share': 1, 'email': ''}]
        })
        event = Event('cool event', [Participant('Kim', 1, uuid='1')], 'id123')
        event.purchases.append(Purchase('1', 1, ['1'], 'label'))
        RepositoryLocator.events().add(event)
        internal_event = internal_events.PurchaseAddedEvent(event.uuid)

        self.handler.execute(internal_event)

        document = self.with_mongomock.collection('eventdetails_view').find_one()
        self.assertEqual('label', document['purchases'][0]['label'])
        self.assertEqual(1, document['purchases'][0]['amount'])
        self.assertEqual('1', document['purchases'][0]['purchaser_id'])


class OnPurchaseAddedUpdateResultTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_memory_repository.before()
        self.with_mongomock.before()
        handlers.DB = self.with_mongomock.db
        self.handler = handlers.OnPurchaseAddedUpdateResult()

    def tearDown(self):
        self.with_memory_repository.after()
        self.with_mongomock.after()
        handlers.DB = None

    def test_the_event_result_is_updated(self):
        event = Event('cool event', [Participant('Kim', 1, uuid='1'), Participant('Lea', 1, uuid='2')], 'id123')
        event.purchases.append(Purchase('1', 1, ['1', '2'], 'label'))
        RepositoryLocator.events().add(event)
        internal_event = internal_events.PurchaseAddedEvent(event.uuid)

        self.handler.execute(internal_event)

        document = self.with_mongomock.collection('eventresult_view').find_one()
        self.assertIsNotNone(document)
        self.assertEqual('id123', document['event_id'])
        self.assertEqual(1, document['detail']['1']['total_spent'])
        self.assertEqual(0, document['detail']['1']['total_debt'])
        self.assertEqual(0.5, document['detail']['2']['debts_detail']['1'])
