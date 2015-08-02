from agoodfriendalwayspayshisdebts.locator import RepositoryLocator


class MemoryRepository:
    def __init__(self):
        self.entities = {}

    def add(self, entity):
        self.entities[entity.id] = entity

    def get(self, _id):
        try:
            entity = self.entities[_id]
        except KeyError:
            return None
        return entity

    def update(self, _id, entity):
        self.entities[_id] = entity


class MemoryEventRepository(MemoryRepository):
    pass


class MemoryRepositoryLocator(RepositoryLocator):
    def __init__(self):
        RepositoryLocator.__init__(self)
        self.memoryEventRepository = MemoryEventRepository()

    def _get_events(self):
        return self.memoryEventRepository
