import abc

import factories


class MongoRepository:
    __metaclass__ = abc.ABCMeta

    def __init__(self, collection):
        self.collection = collection

    @abc.abstractmethod
    def get(self, oid):
        return

    def add(self, entity):
        self.collection.insert(entity.serialize())

    def update(self, oid, entity):
        self.collection.update({'_id': oid}, entity.serialize())


class EventRepository(MongoRepository):
    def get(self, oid):
        document = self.collection.find_one({'_id': oid})
        return factories.EventFactory.create_event_from_document(document)
