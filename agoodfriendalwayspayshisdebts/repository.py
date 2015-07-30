import abc

import factories


class MongoRepository:
    __metaclass__ = abc.ABCMeta

    def __init__(self, collection):
        self.collection = collection

    @abc.abstractmethod
    def get(self, uuid):
        return

    def add(self, entity):
        self.collection.insert(entity.to_bson())

    def update(self, uuid, entity):
        current_entity_document = self.collection.find_one({"uuid": uuid})
        self.collection.update({'_id': current_entity_document['_id']}, entity.to_bson())


class EventRepository(MongoRepository):
    def get(self, uuid):
        document = self.collection.find_one({'uuid': uuid})
        if document is None:
            return None
        return factories.EventFactory.create_event_from_document(document)
