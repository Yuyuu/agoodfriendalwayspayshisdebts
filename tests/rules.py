import abc

import mongomock

from agoodfriendalwayspayshisdebts.bus import EventBus
from agoodfriendalwayspayshisdebts.locator import EventBusLocator, RepositoryLocator
from memory import MemoryRepositoryLocator


class Rule(object):
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def before(self):
        pass

    @abc.abstractmethod
    def after(self):
        pass


class FakeEventBus(EventBus):
    def __init__(self):
        self.events = {}

    def publish(self, event):
        self.events[event.__class__] = event

    def last_event_of_type(self, event_type):
        return self.events[event_type]


class WithEventBus(Rule):
    def __init__(self):
        self.fake_event_bus = None

    def before(self):
        self.fake_event_bus = FakeEventBus()
        EventBusLocator.initialize(self.fake_event_bus)

    def after(self):
        EventBusLocator.initialize(None)


class WithMemoryRepository(Rule):
    def before(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def after(self):
        RepositoryLocator.initialize(None)


class WithMongoMock(Rule):
    def __init__(self):
        self.db = None

    def before(self):
        self.db = mongomock.Connection().db

    def after(self):
        pass

    def collection(self, collection_name):
        return self.db[collection_name]
