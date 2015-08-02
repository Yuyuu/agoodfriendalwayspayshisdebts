import unittest
from uuid import uuid4

from agoodfriendalwayspayshisdebts import repository, model
from rules import WithMongoMock


class FakeEntity:
    def __init__(self, _id=uuid4(), prop=None):
        self.id = _id
        self.prop = prop

    def to_bson(self):
        return {'_id': self.id, 'prop': self.prop}


class FakeEntityRepository(repository.MongoRepository):
    def get(self, _id):
        element = self.collection.find_one({'_id': _id})
        return FakeEntity(element['_id'], element['prop'])


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

        self.assertEqual(entity.id, found_element['_id'])

    def test_can_retrieve_an_entity(self):
        entity = FakeEntity()
        self.repository.add(entity)

        found_entity = self.repository.get(entity.id)

        self.assertIsNotNone(found_entity)

    def test_can_update_an_entity(self):
        entity = FakeEntity()
        self.repository.add(entity)

        entity.prop = 'updated'
        self.repository.update(entity.id, entity)

        self.assertEqual('updated', self.repository.get(entity.id).prop)


class EventRepositoryTestCase(unittest.TestCase):
    with_mongomock = WithMongoMock()

    def setUp(self):
        self.with_mongomock.before()
        self.collection = self.with_mongomock.collection('collection')
        self.repository = repository.EventRepository(self.collection)
        self.kim = model.Participant('Kim', 1)
        self.lea = model.Participant('Lea', 1)

    def tearDown(self):
        self.with_mongomock.after()

    def test_can_add_an_event(self):
        event = model.Event('Cool event', [self.kim, self.lea])
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
        event = model.Event('Cool event', [self.kim, self.lea])
        event.purchases.append(model.Purchase(self.kim.id, 5, [self.kim.id], 'Shopping'))
        self.repository.add(event)

        found_event = self.repository.get(event.id)

        self.assertEqual(event.id, found_event.id)
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
        event = model.Event('Cool event', [self.kim, self.lea])
        self.repository.add(event)

        event.name = 'Weekend at the beach'
        self.repository.update(event.id, event)

        updated_event = self.repository.get(event.id)
        self.assertEqual('Weekend at the beach', updated_event.name)
