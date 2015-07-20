import unittest
import mongomock

import repository
import events


class FakeEntity:
    def __init__(self, uuid, oid=mongomock.ObjectId()):
        self.oid = oid
        self.uuid = uuid

    def serialize(self):
        return {'_id': self.oid, 'id': self.uuid}


class FakeEntityRepository(repository.MongoRepository):
    def get(self, uuid):
        element = self.collection.find_one({'id': str(uuid)})
        return FakeEntity(element['id'], element['_id'])


class MongoRepositoryTestCase(unittest.TestCase):
    def setUp(self):
        self.collection = mongomock.Connection().db['collection']
        self.repository = FakeEntityRepository(self.collection)

    def test_can_add_an_entity(self):
        entity = FakeEntity('1234')
        self.repository.add(entity)

        found_element = self.collection.find_one()

        self.assertEqual('1234', found_element['id'])

    def test_can_retrieve_an_entity(self):
        entity = FakeEntity('1234')
        self.repository.add(entity)

        found_entity = self.repository.get('1234')

        self.assertIsNotNone(found_entity)

    def test_can_update_an_entity(self):
        entity = FakeEntity('1234')
        self.repository.add(entity)

        entity.uuid = '5678'
        self.repository.update(entity.oid, entity)

        self.assertIsNotNone(self.repository.get('5678'))


class EventRepositoryTestCase(unittest.TestCase):
    def setUp(self):
        self.collection = mongomock.Connection().db['collection']
        self.repository = repository.EventRepository(self.collection)

    def test_can_add_an_event(self):
        event = events.Event('Cool event', [events.Participant('Kim', 1), events.Participant('Lea', 1)])
        self.repository.add(event)

        found_event = self.collection.find_one()

        self.assertIsNotNone(found_event['_id'])
        self.assertEqual('Cool event', found_event['name'])
        self.assertListEqual([
            {'name': 'Kim', 'email': '', 'share': 1},
            {'name': 'Lea', 'email': '', 'share': 1}
        ], found_event['participants'])
        self.assertEqual(0, len(found_event['purchases']))

    def test_can_retrieve_an_event(self):
        event = events.Event('Cool event', [events.Participant('Kim', 1), events.Participant('Lea', 1)])
        event.add_purchase(events.Purchase('Kim', 'Shopping', 5))
        self.repository.add(event)

        found_event = self.repository.get(event.oid)

        self.assertEqual(event.oid, found_event.oid)
        self.assertEqual('Cool event', found_event.name)
        self.assertEqual('Kim', found_event.participants[0].name)
        self.assertEqual(1, found_event.participants[0].share)
        self.assertEqual('Lea', found_event.participants[1].name)
        self.assertEqual(1, found_event.participants[1].share)
        self.assertEqual('Kim', found_event.purchases[0].purchaser)
        self.assertEqual('Shopping', found_event.purchases[0].title)
        self.assertEqual(5, found_event.purchases[0].amount)

    def test_can_update_an_event(self):
        event = events.Event('Cool event', [events.Participant('Kim', 1), events.Participant('Lea', 1)])
        event.add_purchase(events.Purchase('Kim', 'Shopping', 5))
        self.repository.add(event)

        event.name = 'Weekend at the beach'
        self.repository.update(event.oid, event)

        updated_event = self.repository.get(event.oid)
        self.assertEqual('Weekend at the beach', updated_event.name)
