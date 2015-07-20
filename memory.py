from locator import RepositoryLocator


class MemoryRepository:
    def __init__(self):
        self.entities = {}

    def add(self, entity):
        self.entities[entity.oid] = entity

    def get(self, oid):
        return self.entities[oid]

    def update(self, oid, entity):
        self.entities[oid] = entity


class MemoryEventRepository(MemoryRepository):
    pass


class MemoryRepositoryLocator(RepositoryLocator):
    def __init__(self):
        RepositoryLocator.__init__(self)
        self.memoryEventRepository = MemoryEventRepository()

    def _get_events(self):
        return self.memoryEventRepository
