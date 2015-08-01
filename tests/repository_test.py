import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts import repository, events
from rules import WithMongoMock


class FakeEntity:
    def __init__(self, uuid=uuid4()):
        self.uuid = uuid

    def to_bson(self):
        return {'uuid': self.uuid}


class FakeEntityRepository(repository.MongoRepository):
    def get(self, uuid):
        element = self.collection.find_one({'uuid': uuid})
        return FakeEntity(element['uuid'])


class MongoRepositoryTestCase(unittest.TestCase):
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_mongomock.before()
        self.collection = self.with_mongomock.collection('collection')
        self.repository = FakeEntityRepository(self.collection)

    def tearDown(self):
        self.with_mongomock.after()

    def test_can_add_an_entity(self):
        entity = FakeEntity()
        self.repository.add(entity)

        found_element = self.collection.find_one()

        self.assertEqual(entity.uuid, found_element['uuid'])

    def test_can_retrieve_an_entity(self):
        entity = FakeEntity()
        self.repository.add(entity)

        found_entity = self.repository.get(entity.uuid)

        self.assertIsNotNone(found_entity)

    def test_can_update_an_entity(self):
        initial_uuid = uuid4()
        entity = FakeEntity(initial_uuid)
        self.repository.add(entity)

        entity.uuid = uuid4()
        self.repository.update(initial_uuid, entity)

        self.assertIsNotNone(self.repository.get(entity.uuid))


class EventRepositoryTestCase(unittest.TestCase):
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_mongomock.before()
        self.collection = self.with_mongomock.collection('collection')
        self.repository = repository.EventRepository(self.collection)
        self.kim = events.Participant('Kim', 1)
        self.lea = events.Participant('Lea', 1)

    def tearDown(self):
        self.with_mongomock.after()

    def test_can_add_an_event(self):
        event = events.Event('Cool event', [self.kim, self.lea])
        self.repository.add(event)

        found_event = self.collection.find_one()

        self.assertIsNotNone(found_event['_id'])
        self.assertEqual('Cool event', found_event['name'])
        self.assertListEqual([
            {'id': self.kim.id, 'name': 'Kim', 'email': '', 'share': 1},
            {'id': self.lea.id, 'name': 'Lea', 'email': '', 'share': 1}
        ], found_event['participants'])
        self.assertEqual(0, len(found_event['purchases']))

    def test_can_retrieve_an_event(self):
        event = events.Event('Cool event', [self.kim, self.lea])
        event.purchases.append(events.Purchase(self.kim.id, 5, [self.kim.id], 'Shopping'))
        self.repository.add(event)

        found_event = self.repository.get(event.uuid)

        self.assertEqual(event.uuid, found_event.uuid)
        self.assertEqual('Cool event', found_event.name)
        self.assertEqual(self.kim.id, found_event.participants[0].id)
        self.assertEqual('Kim', found_event.participants[0].name)
        self.assertEqual(1, found_event.participants[0].share)
        self.assertEqual(self.lea.id, found_event.participants[1].id)
        self.assertEqual('Lea', found_event.participants[1].name)
        self.assertEqual(1, found_event.participants[1].share)
        self.assertEqual(self.kim.id, found_event.purchases[0].purchaser_id)
        self.assertListEqual([self.kim.id], found_event.purchases[0].participants_ids)
        self.assertEqual('Shopping', found_event.purchases[0].label)
        self.assertEqual(5, found_event.purchases[0].amount)

    def test_can_update_an_event(self):
        event = events.Event('Cool event', [self.kim, self.lea])
        self.repository.add(event)

        event.name = 'Weekend at the beach'
        self.repository.update(event.uuid, event)

        updated_event = self.repository.get(event.uuid)
        self.assertEqual('Weekend at the beach', updated_event.name)
