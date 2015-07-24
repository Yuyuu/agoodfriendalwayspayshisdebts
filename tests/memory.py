from agoodfriendalwayspayshisdebts.locator import RepositoryLocator


class MemoryRepository:
    def __init__(self):
        self.entities = {}

    def add(self, entity):
        self.entities[entity.uuid] = entity

    def get(self, uuid):
        return self.entities[uuid]

    def update(self, uuid, entity):
        self.entities[uuid] = entity


class MemoryEventRepository(MemoryRepository):
    pass


class MemoryRepositoryLocator(RepositoryLocator):
    def __init__(self):
        RepositoryLocator.__init__(self)
        self.memoryEventRepository = MemoryEventRepository()

    def _get_events(self):
        return self.memoryEventRepository
