import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
from agoodfriendalwayspayshisdebts import command_handlers, commands, events
from rules import WithEventBus, WithMemoryRepository


def fake_event(uuid=uuid4()):
    event = events.Event('Cool event', [events.Participant('Kim', 1)], uuid)
    return event


class CreateEventCommandHandlerTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()
    with_event_bus = WithEventBus()

    def setUp(self):
        self.with_memory_repository.before()
        self.with_event_bus.before()

    def tearDown(self):
        self.with_memory_repository.after()
        self.with_event_bus.after()

    def test_the_event_is_added_to_the_repository(self):
        handler = command_handlers.CreateEventCommandHandler(commands.CreateEventCommand)

        event_id = handler.execute(commands.CreateEventCommand('Cool event', [
            {'name': 'Lea', 'email': 'lea@email.com', 'share': 1},
            {'name': 'Eva', 'share': 1}
        ]))

        entity = RepositoryLocator.events().entities[event_id]
        self.assertEqual('Cool event', entity.name)
        self.assertEqual('Lea', entity.participants[0].name)
        self.assertEqual('lea@email.com', entity.participants[0].email)
        self.assertEqual(1, entity.participants[0].share)
        self.assertEqual('Eva', entity.participants[1].name)
        self.assertEqual('', entity.participants[1].email)
        self.assertEqual(1, entity.participants[1].share)


class AddPurchaseCommandHandlerTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()
    with_event_bus = WithEventBus()

    def setUp(self):
        self.with_memory_repository.before()
        self.with_event_bus.before()
        self.event = fake_event()
        RepositoryLocator.events().entities[self.event.uuid] = self.event

    def tearDown(self):
        self.with_memory_repository.after()
        self.with_event_bus.after()

    def test_the_purchase_is_added_to_the_repository(self):
        bob = events.Participant('Bob', 1)
        self.event.add_participant(bob)
        handler = command_handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        command = commands.AddPurchaseCommand(self.event.uuid, self.event.participants[0].id, 'Gas', 10)
        command.participants_ids = [str(bob.id)]
        command.description = '10km at 1e/km'

        handler.execute(command)

        entity = RepositoryLocator.events().entities[self.event.uuid]

        self.assertEqual(self.event.participants[0].id, entity.purchases[0].purchaser_id)
        self.assertEqual('Gas', entity.purchases[0].label)
        self.assertEqual(10, entity.purchases[0].amount)
        self.assertListEqual([bob.id], entity.purchases[0].participants_ids)
        self.assertEqual('10km at 1e/km', entity.purchases[0].description)

    def test_the_purchase_is_shared_between_all_participants_if_none_is_specified(self):
        RepositoryLocator.events().entities[self.event.uuid].participants.append(events.Participant('Bob', 1))
        handler = command_handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        command = commands.AddPurchaseCommand(self.event.uuid, self.event.participants[0].id, 'Gas', 10)

        handler.execute(command)

        entity = RepositoryLocator.events().entities[self.event.uuid]

        self.assertEqual(2, len(entity.purchases[0].participants_ids))
