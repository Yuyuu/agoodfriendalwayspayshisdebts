import abc


class MongoRepository:
    __metaclass__ = abc.ABCMeta

    def __init__(self, collection):
        self.collection = collection

    @abc.abstractmethod
    def get(self, uuid):
        return

    def add(self, entity):
        self.collection.insert(entity.serialize())