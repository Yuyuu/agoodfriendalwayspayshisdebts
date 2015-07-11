import abc

from bson.objectid import ObjectId

from locator import RepositoryLocator
import events


class Handler:
    __metaclass__ = abc.ABCMeta

    def __init__(self, message_type):
        self.message_type = message_type

    @abc.abstractmethod
    def execute(self, message):
        return


class CreateEventCommandHandler(Handler):
    def execute(self, command):
        event = events.Event(command.name, command.participants)
        RepositoryLocator.events().add(event)
        return event.oid


class SearchEventDetailsHandler(Handler):
    def execute(self, search):
        oid = ObjectId(search.id_as_string)
        event = RepositoryLocator.events().get(oid)
        return event
