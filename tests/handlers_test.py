import unittest

from locator import RepositoryLocator
from memory import MemoryRepositoryLocator
import commands
import handlers


class CreateEventCommandHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_the_event_is_added_to_the_repository(self):
        handler = handlers.CreateEventCommandHandler()

        event_id = handler.execute(commands.CreateEventCommand('Cool event', ['Lea', 'Eva']))

        repository = RepositoryLocator.events()
        self.assertEqual('Cool event', repository.entities[event_id].name)
        self.assertEqual(['Lea', 'Eva'], repository.entities[event_id].participants)