import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
from memory import MemoryRepositoryLocator
from agoodfriendalwayspayshisdebts import handlers, searches, commands, events


def fake_event(uuid=uuid4()):
    event = events.Event('Cool event', [events.Participant('Kim', 1)], uuid)
    return event


class CreateEventCommandHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_the_event_is_added_to_the_repository(self):
        handler = handlers.CreateEventCommandHandler(commands.CreateEventCommand)

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
    event_uuid = uuid4()

    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())
        RepositoryLocator.events().entities[self.event_uuid] = fake_event(self.event_uuid)

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_the_purchase_is_added_to_the_repository(self):
        handler = handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        command = commands.AddPurchaseCommand(str(self.event_uuid), 'Kim', 'Gas', 10)
        command.participants = ['Bob']
        command.description = '10km at 1e/km'

        handler.execute(command)

        entity = RepositoryLocator.events().entities[self.event_uuid]

        self.assertEqual('Kim', entity.purchases[0].purchaser)
        self.assertEqual('Gas', entity.purchases[0].label)
        self.assertEqual(10, entity.purchases[0].amount)
        self.assertListEqual(['Bob'], entity.purchases[0].participants)
        self.assertEqual('10km at 1e/km', entity.purchases[0].description)

    def test_the_purchase_is_shared_between_all_participants_if_none_is_specified(self):
        RepositoryLocator.events().entities[self.event_uuid].participants.append(events.Participant('Bob', 1))
        handler = handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        command = commands.AddPurchaseCommand(str(self.event_uuid), 'Kim', 'Gas', 10)

        handler.execute(command)

        entity = RepositoryLocator.events().entities[self.event_uuid]

        self.assertListEqual(['Kim', 'Bob'], entity.purchases[0].participants)


class EventDetailsSearchHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_can_return_an_event_and_its_properties(self):
        event = fake_event()
        event.add_participant(events.Participant('Joe', '1'))
        RepositoryLocator.events().entities[event.uuid] = event

        result = handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)\
            .execute(searches.EventDetailsSearch(str(event.uuid)))

        self.assertEqual(result.uuid, event.uuid)
        self.assertEqual(result.name, 'Cool event')
        self.assertEqual(result.participants[0].name, 'Kim')
        self.assertListEqual(result.purchases, [])

    def test_an_exception_is_thrown_if_the_given_uuid_is_not_valid(self):
        handler = handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)

        self.assertRaises(ValueError, handler.execute, searches.EventDetailsSearch("hello"))


class SearchEventDebtsResultHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_can_return_the_debts_result_of_the_event(self):
        event = fake_event()
        event.add_participant(events.Participant('Joe', 1))
        event.add_purchase(events.Purchase('Kim', 10, ['Kim', 'Joe'], '1'))
        event.add_purchase(events.Purchase('Joe', 6, ['Kim', 'Joe'], '2'))
        RepositoryLocator.events().entities[event.uuid] = event

        result = handlers.SearchEventDebtsResultHandler(searches.EventDebtsResultSearch)\
            .execute(searches.EventDebtsResultSearch(str(event.uuid)))

        self.assertEqual(10, result.of('Kim').total_spent)
        self.assertEqual(0, result.of('Kim').get_debt_towards('Joe'))
        self.assertEqual(0, result.of('Kim').total_debt)
        self.assertEqual(6, result.of('Joe').total_spent)
        self.assertEqual(2, result.of('Joe').get_debt_towards('Kim'))
        self.assertEqual(2, result.of('Joe').total_debt)
