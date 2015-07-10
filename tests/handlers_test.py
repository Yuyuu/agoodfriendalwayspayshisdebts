import unittest

from locator import RepositoryLocator
from memory import MemoryRepositoryLocator
import commands
import searches
import handlers


class CreateEventCommandHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_the_event_is_added_to_the_repository(self):
        handler = handlers.CreateEventCommandHandler(commands.CreateEventCommand)

        event_id = handler.execute(commands.CreateEventCommand('Cool event', ['Lea', 'Eva']))

        repository = RepositoryLocator.events()
        self.assertEqual('Cool event', repository.entities[event_id].name)
        self.assertEqual(['Lea', 'Eva'], repository.entities[event_id].participants)


class EventDetailsSearchHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_can_return_an_event_and_its_properties(self):
        event = {'_id': '1234', 'name': 'Cool event', 'participants': ['Kim'], 'purchases': []}
        RepositoryLocator.events().entities['1234'] = event

        result = handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)\
            .execute(searches.EventDetailsSearch('1234'))

        self.assertDictEqual(result, event)

