import abc

from locator import RepositoryLocator
import events


class CommandHandler:
    def __init__(self):
        pass

    @abc.abstractmethod
    def execute(self, command):
        return


class CreateEventCommandHandler(CommandHandler):
    def execute(self, command):
        event = events.Event(command.name, command.participants)
        RepositoryLocator.events().add(event)
        return event.oid