import unittest

from rules import WithMemoryRepository, WithMongoMock
import agoodfriendalwayspayshisdebts.event_search_handlers as handlers
from agoodfriendalwayspayshisdebts.events import Event
from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
import agoodfriendalwayspayshisdebts.internal_events as internal_events


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
        event = Event('cool event', [], 'id123')
        internal_event = internal_events.EventCreatedEvent(event.uuid)
        RepositoryLocator.events().add(event)

        self.handler.execute(internal_event)

        document = self.with_mongomock.collection('eventdetails_view').find_one()
        self.assertIsNotNone(document)
        self.assertEqual('id123', document['uuid'])
        self.assertEqual('cool event', document['name'])
