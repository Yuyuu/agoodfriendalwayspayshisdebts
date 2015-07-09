import abc

import events


class MongoRepository:
    __metaclass__ = abc.ABCMeta

    def __init__(self, collection):
        self.collection = collection

    @abc.abstractmethod
    def get(self, uuid):
        return

    def add(self, entity):
        self.collection.insert(entity.serialize())


class EventRepository(MongoRepository):
    def get(self, uuid):
        element = self.collection.find_one({'_id': uuid})
        event = events.Event(element['name'], element['participants'])
        event.oid = uuid
        purchases = []
        for purchase in element['purchases']:
            purchases.append(events.Purchase(purchase['purchaser'], purchase['title'], purchase['amount']))
        event.purchases = purchases
        return event