import unittest
import mongomock

import repository


class FakeEntity():
    def __init__(self, uuid):
        self.uuid = uuid

    def serialize(self):
        return {'id': str(self.uuid)}


class FakeEntityRepository(repository.MongoRepository):
    def get(self, uuid):
        element = self.collection.find_one({'id': str(uuid)})
        return FakeEntity(element['id'])


class MongoRepositoryTestCase(unittest.TestCase):
    def setUp(self):
        self.collection = mongomock.Connection().db['agoodfriendalwayspayshisdebts']
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