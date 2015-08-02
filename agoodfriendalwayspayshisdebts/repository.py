import abc

import factories


class MongoRepository:
    __metaclass__ = abc.ABCMeta

    def __init__(self, collection):
        self.collection = collection

    @abc.abstractmethod
    def get(self, _id):
        return

    def add(self, entity):
        self.collection.insert(entity.to_bson())

    def update(self, _id, entity):
        current_entity_document = self.collection.find_one({"_id": _id})
        self.collection.update({'_id': current_entity_document['_id']}, entity.to_bson())


class EventRepository(MongoRepository):
    def get(self, _id):
        document = self.collection.find_one({'_id': _id})
        if document is None:
            return None
        return factories.EventFactory.create_event_from_document(document)
