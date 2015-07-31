import abc

import repository

db = None


class RepositoryLocator(object):
    instance = None

    @staticmethod
    def initialize(repository_locator):
        RepositoryLocator.instance = repository_locator

    @staticmethod
    def events():
        return RepositoryLocator.instance._get_events()

    @abc.abstractmethod
    def _get_events(self):
        return


class MongoRepositoryLocator(RepositoryLocator):
    def _get_events(self):
        return repository.EventRepository(db['event'])


class EventBusLocator(object):
    instance = None

    @staticmethod
    def get_instance():
        return EventBusLocator.instance

    @staticmethod
    def initialize(instance):
        EventBusLocator.instance = instance
