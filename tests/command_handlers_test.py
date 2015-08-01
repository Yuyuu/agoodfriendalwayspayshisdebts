import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
from agoodfriendalwayspayshisdebts import command_handlers, searches, commands, events
from agoodfriendalwayspayshisdebts.errors import InvalidUUIDError, EntityNotFoundError
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

    def setUp(self):
        self.with_memory_repository.before()
        self.event = fake_event()
        RepositoryLocator.events().entities[self.event.uuid] = self.event

    def tearDown(self):
        self.with_memory_repository.after()

    def test_the_purchase_is_added_to_the_repository(self):
        bob = events.Participant('Bob', 1)
        self.event.add_participant(bob)
        handler = command_handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        command = commands.AddPurchaseCommand(str(self.event.uuid), str(self.event.participants[0].id), 'Gas', 10)
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
        command = commands.AddPurchaseCommand(str(self.event.uuid), str(self.event.participants[0].id), 'Gas', 10)

        handler.execute(command)

        entity = RepositoryLocator.events().entities[self.event.uuid]

        self.assertEqual(2, len(entity.purchases[0].participants_ids))


class EventDetailsSearchHandlerTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()

    def setUp(self):
        self.with_memory_repository.before()

    def tearDown(self):
        self.with_memory_repository.after()

    def test_can_return_an_event_and_its_properties(self):
        event = fake_event()
        event.add_participant(events.Participant('Joe', '1'))
        RepositoryLocator.events().entities[event.uuid] = event

        result = command_handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)\
            .execute(searches.EventDetailsSearch(str(event.uuid)))

        self.assertEqual(result.uuid, event.uuid)
        self.assertEqual(result.name, 'Cool event')
        self.assertEqual(result.participants[0].name, 'Kim')
        self.assertListEqual(result.purchases, [])

    def test_an_error_is_raised_if_the_given_uuid_is_not_valid(self):
        handler = command_handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)

        self.assertRaises(InvalidUUIDError, handler.execute, searches.EventDetailsSearch("hello"))

    def test_an_error_is_raised_if_the_event_does_not_exist(self):
        handler = command_handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)

        self.assertRaises(EntityNotFoundError, handler.execute, searches.EventDetailsSearch(str(uuid4())))


class SearchEventDebtsResultHandlerTestCase(unittest.TestCase):
    with_memory_repository = WithMemoryRepository()

    def setUp(self):
        self.with_memory_repository.before()

    def tearDown(self):
        self.with_memory_repository.after()

    def test_can_return_the_debts_result_of_the_event(self):
        event = fake_event()
        kim = event.participants[0]
        joe = events.Participant('Joe', 1)
        event.add_participant(joe)
        event.add_purchase(events.Purchase(kim.id, 10, [kim.id, joe.id], '1'))
        event.add_purchase(events.Purchase(joe.id, 6, [kim.id, joe.id], '2'))
        RepositoryLocator.events().entities[event.uuid] = event

        result = command_handlers.SearchEventDebtsResultHandler(searches.EventDebtsResultSearch)\
            .execute(searches.EventDebtsResultSearch(str(event.uuid)))

        self.assertEqual(10, result.of(kim.id).total_spent)
        self.assertEqual(0, result.of(kim.id).get_debt_towards(joe.id))
        self.assertEqual(0, result.of(kim.id).total_debt)
        self.assertEqual(6, result.of(joe.id).total_spent)
        self.assertEqual(2, result.of(joe.id).get_debt_towards(kim.id))
        self.assertEqual(2, result.of(joe.id).total_debt)

    def test_an_error_is_raised_if_the_given_uuid_is_not_valid(self):
        handler = command_handlers.SearchEventDebtsResultHandler(searches.EventDebtsResultSearch)

        self.assertRaises(InvalidUUIDError, handler.execute, searches.EventDebtsResultSearch("hello"))

    def test_an_error_is_raised_if_the_event_does_not_exist(self):
        handler = command_handlers.SearchEventDebtsResultHandler(searches.EventDebtsResultSearch)

        self.assertRaises(EntityNotFoundError, handler.execute, searches.EventDebtsResultSearch(str(uuid4())))
