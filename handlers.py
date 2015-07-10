import abc

from locator import RepositoryLocator
import events


class CommandHandler:
    def __init__(self, command_type):
        self.command_type = command_type

    @abc.abstractmethod
    def execute(self, command):
        return


class CreateEventCommandHandler(CommandHandler):
    def execute(self, command):
        event = events.Event(command.name, command.participants)
        RepositoryLocator.events().add(event)
        return event.oid


class SearchHandler:
    def __init__(self, search_type):
        self.search_type = search_type

    @abc.abstractmethod
    def execute(self, command):
        return


class SearchEventDetailsHandler(SearchHandler):
    def execute(self, command):
        event = RepositoryLocator.events().get(command.oid)
        return event
